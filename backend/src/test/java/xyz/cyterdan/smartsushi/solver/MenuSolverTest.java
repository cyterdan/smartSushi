package xyz.cyterdan.smartsushi.solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import xyz.cyterdan.smartsushi.model.Dish;
import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.MenuItemBuilder;
import xyz.cyterdan.smartsushi.model.Order;
import xyz.cyterdan.smartsushi.model.Requirements;
import xyz.cyterdan.smartsushi.model.RequirementsBuilder;

/**
 *
 * @author cytermann
 */
public class MenuSolverTest {

    private Set<MenuItem> menu;

    public MenuSolverTest() {
    }

    @Before
    public void setUp() {
        menu = new HashSet<>();
        menu.add(
                new MenuItemBuilder()
                .name("2 x dish1")
                .price(10.0)
                .addDish(new Dish("dish1"), 2)
                .build()
        );
        menu.add(
                new MenuItemBuilder()
                .name("2 x dish2")
                .price(8.0)
                .addDish(new Dish("dish2"), 2)
                .build()
        );
        menu.add(
                new MenuItemBuilder()
                .name("4x dish1 + 4x dish2")
                .price(20.0)
                .addDish(new Dish("dish1"), 4)
                .addDish(new Dish("dish2"), 4)
                .build()
        );
    }

    @Test
    public void testSolve() {

        Map<Dish, Integer> expected = new HashMap<>();
        expected.put(new Dish("dish1"), 10);
        expected.put(new Dish("dish2"), 5);

        RequirementsBuilder requirementsBuilder = new RequirementsBuilder();
        for (Dish dish : expected.keySet()) {
            requirementsBuilder.addRequirement(dish, expected.get(dish));
        }
        Requirements requirements = requirementsBuilder.build();

        MenuSolver solver = new MenuSolver(menu, requirements);

        solver.solve(true);

        Order order = solver.getOrder();

        Assert.assertEquals(Double.valueOf(50.0), order.calculateCost());
        
        Map<Dish, Integer> orderedDishes = new HashMap<>();
        for (MenuItem menuItem : order.getOrder().keySet()) {
            for (Dish dish : menuItem.getItems().keySet()) {
                int quantity = menuItem.getItems().get(dish) * order.getOrder().get(menuItem);
                if (orderedDishes.containsKey(dish)) {
                    orderedDishes.put(dish, orderedDishes.get(dish) + quantity);
                } else {
                    orderedDishes.put(dish, quantity);
                }
            }

        }
        for (Dish dish : expected.keySet()) {
            Assert.assertTrue(orderedDishes.get(dish) >= expected.get(dish));

        }

    }

}
