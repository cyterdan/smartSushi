package xyz.cyterdan.smartsushi;

import xyz.cyterdan.smartsushi.solver.MenuSolver;
import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.RequirementsBuilder;
import xyz.cyterdan.smartsushi.model.Requirements;
import xyz.cyterdan.smartsushi.data.GoogleDriveMenuLoader;
import xyz.cyterdan.smartsushi.model.Dish;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import xyz.cyterdan.smartsushi.data.MenuLoader;

/**
 * @author cytermann
 */
public class JapaneseMenu {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("loading menu...");
        
        String spreadsheetId = "1W9ZqwvalNYdbUkLfRv98bq21lGLqdjUoUkTLjPQBDyM";
               
        MenuLoader loader = new GoogleDriveMenuLoader(spreadsheetId,"A3:A", "B1:BR");

        Set<MenuItem> menu = loader.load();

        Map<String, Dish> dishes = loader.getDishes();
        System.out.println("loaded.");

        RequirementsBuilder requirementsBuilder = new RequirementsBuilder();

        Requirements requirements = requirementsBuilder
                .addRequirement(dishes.get("Soupe"), 2)
                .addRequirement(dishes.get("Salade"), 3)
                .addRequirement(dishes.get("Sashimi Saumon"), 12)
                .addRequirement(dishes.get("Sushi Saumon"), 14)
                .addRequirement(dishes.get("California Avocat Saumon"), 12)
                .addRequirement(dishes.get("Riz"), 1)
                .build();

        MenuSolver solver = new MenuSolver(menu, requirements);

        System.out.println("solving menu...");
        
        solver.solve(true);

        System.out.println("best order is " + solver.getOrder().getOrder() + " total cost will be " + solver.getOrder().calculateCost());
        System.out.println("You will get these item as a bonus : " + solver.getOrder().getBonus(requirements));

    }

}
