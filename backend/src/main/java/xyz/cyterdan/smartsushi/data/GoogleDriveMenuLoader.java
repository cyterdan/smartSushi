package xyz.cyterdan.smartsushi.data;

import xyz.cyterdan.smartsushi.model.Dish;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.MenuItemBuilder;

/**
 * Load a menu from Google drive sheets
 *
 * @author cytermann
 */
public class GoogleDriveMenuLoader implements MenuLoader {

    private String spreadsheetId;
    /**
     * spreadsheet range where dishes are found (A1 notation)
     */
    private String dishesRange;
    /**
     * spreadsheet range where menu is (A1 notation)
     */
    private String menuRange;

    /**
     * store the dishes
     */
    private final Map<String, Dish> dishes;

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME
            = "SmartSushi";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/" + APPLICATION_NAME);

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES
            = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in
                = Files.newInputStream(new File(System.getProperty("user.home"), ".credentials/" + APPLICATION_NAME + "/client_secret_smart_sushi.json").toPath());
        GoogleClientSecrets clientSecrets
                = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow
                = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public GoogleDriveMenuLoader(String spreadsheetId, String dishesRange, String menuRange) {
        dishes = new HashMap<>();
        this.spreadsheetId = spreadsheetId;
        this.dishesRange = dishesRange;
        this.menuRange = menuRange;

    }

    @Override
    public Set<MenuItem> load()  {
        Sheets service = null;
        try {
            service = getSheetsService();
        } catch (IOException ex) {
            Logger.getLogger(GoogleDriveMenuLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        ValueRange response = null;
        try {
            response = service.spreadsheets().values().get(spreadsheetId, dishesRange).execute();
        } catch (IOException ex) {
            Logger.getLogger(GoogleDriveMenuLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<List<Object>> values = response.getValues();

        List<String> dishIndex = new ArrayList<>();

        for (List<Object> row : values) {
            String dishName = row.get(0).toString().trim();
            dishes.put(dishName, new Dish(dishName));
            dishIndex.add(dishName);
        }
        try {
            response = service.spreadsheets().values().get(spreadsheetId, menuRange).execute();
        } catch (IOException ex) {
            Logger.getLogger(GoogleDriveMenuLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        values = response.getValues();

        int nbMenus = values.get(0).size();

        Set<MenuItem> menu = new HashSet<>();

        for (int m = 0; m < nbMenus; m++) {
            String name = values.get(0).get(m).toString();
            Double price = Double.valueOf(values.get(1).get(m).toString().replace("€", "").replace(",", ".").trim());
            MenuItemBuilder menuItemBuilder = new MenuItemBuilder();
            menuItemBuilder.name(name);
            menuItemBuilder.price(price);

            for (int d = 0; d < dishIndex.size(); d++) {
                Integer quantity = Integer.valueOf(values.get(2 + d).get(m).toString());
                if (quantity > 0) {
                    menuItemBuilder.addDish(dishes.get(dishIndex.get(d)), quantity);
                }
            }
            menu.add(menuItemBuilder.build());

        }
        return menu;

    }

    @Override
    public Map<String, Dish> getDishes() {
        return dishes;

    }

}
