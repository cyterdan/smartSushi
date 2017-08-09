package xyz.cyterdan.smartsushi.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.On;
import xyz.cyterdan.smartsushi.data.GoogleDriveMenuLoader;
import xyz.cyterdan.smartsushi.data.MenuLoader;
import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.Requirements;
import xyz.cyterdan.smartsushi.solver.MenuSolver;

/**
 *
 * @author cytermann
 */
public class RestApi {

    private static final HashMap<String, MenuLoader> MENU_CACHE = new HashMap<>();

    static {
        MENU_CACHE.put(
                "1W9ZqwvalNYdbUkLfRv98bq21lGLqdjUoUkTLjPQBDyM",
                new GoogleDriveMenuLoader("1W9ZqwvalNYdbUkLfRv98bq21lGLqdjUoUkTLjPQBDyM", "A3:A", "B1:BR")
        );
    }

    public static void main(String[] args) throws IOException {

        Map<String, String> menus = new HashMap<>();

        menus.put("1W9ZqwvalNYdbUkLfRv98bq21lGLqdjUoUkTLjPQBDyM", "Itouya");

        On.get("/menus").json(
                (Req req, Resp response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    return menus.entrySet();
                }
        );

        On.get("/menu").json(
                (String menuId, Req req, Resp resp) -> {
                    resp.header("Access-Control-Allow-Origin", "*");

                    MenuLoader loader = MENU_CACHE.get(menuId);
                    Map<String, Object> response = new HashMap<>();
                    loader.load();
                    response.put("dishes", loader.getDishes().keySet().stream().sorted().collect(Collectors.toList()));
                    return response;
                }
        );

        On.post("/solve").json(
                (String menuId, Req req, Resp resp) -> {
                    resp.header("Access-Control-Allow-Origin", "*");

                    MenuLoader loader = MENU_CACHE.get(menuId);
                    Set<MenuItem> menu = loader.load();
                    Requirements requirements = Utils.fromHtmlParam(req.posted());
 
                    MenuSolver solver = new MenuSolver(menu, requirements);
                    solver.solve(true);
                    

                    Map<String, Object> response = new HashMap<>();
                    response.put("price", solver.getOrder().calculateCost());
                    response.put("order", solver.getOrder().getOrder());
                    response.put("bonus", solver.getOrder().getBonus(requirements));
                    return response;
                }
        );

    }

   

}
