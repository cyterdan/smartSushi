package xyz.cyterdan.smartsushi.solver;

import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.RequirementsBuilder;
import xyz.cyterdan.smartsushi.model.Requirements;
import xyz.cyterdan.smartsushi.model.Order;
import xyz.cyterdan.smartsushi.model.Dish;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Solve menu
 *
 *
 * @author cytermann
 */
public class MenuSolver {

    private final Set<MenuItem> menu;

    /**
     * cache the already sorted menus
     */
    private final Map<Dish, List<MenuItem>> menusSortedByDish;
    private final Requirements requirements;
    private double minimumCost = Double.MAX_VALUE;
    private Order order;

    private final Set<Map<MenuItem, Integer>> visited = new HashSet<>();

    public MenuSolver(Set<MenuItem> menu, Requirements requirements) {
        this.menu = menu;
        this.requirements = new Requirements(requirements);
        this.menusSortedByDish = new HashMap<>();

    }

    /**
     * calculate a price for a group of menu items
     */
    private Double calculatePrice(Map<MenuItem, Integer> items) {
        double sum = 0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            sum += entry.getKey().getPrice() * entry.getValue();
        }
        return sum;
    }

    public void solve(boolean splitToSubproblems) {

        //we can partition the problem into subproblems that have no common dish
        List<Order> subOrders = new ArrayList<>();
        boolean done = false;
        if (splitToSubproblems) {
            while (!requirements.getRequirements().isEmpty() && !done) {
                RequirementsBuilder subRequirementsBuilder = new RequirementsBuilder();
                Set<MenuItem> subMenu = new HashSet<>();
                //find all menuItem that have at least one element in common
                for (MenuItem item : menu) {
                    for (MenuItem item2 : menu) {
                        if (!item.equals(item2)) {
                            Set<Dish> intersection = new HashSet<>(item2.getItems().keySet());
                            intersection.retainAll(item.getItems().keySet());
                            if (!intersection.isEmpty()) {
                                subMenu.add(item2);
                                subMenu.add(item);
                                Set<Dish> union = new HashSet<>(item.getItems().keySet());
                                union.addAll(item2.getItems().keySet());
                                Set<Dish> requirementIntersection = new HashSet<>(union);
                                requirementIntersection.retainAll(requirements.getRequirements().keySet());
                                for (Dish dish : requirementIntersection) {
                                    subRequirementsBuilder.addRequirement(dish, requirements.getRequirements().get(dish));
                                    requirements.getRequirements().remove(dish);
                                }

                            }

                        }
                    }
                }
                Requirements subRequirements = subRequirementsBuilder.build();

                menu.removeAll(subMenu);

                if (subMenu.isEmpty()) {
                    subMenu = menu;
                    subRequirements = requirements;
                    done = true;
                }

                MenuSolver subProblem = new MenuSolver(subMenu, subRequirements);
                subProblem.solve(false);
                subOrders.add(subProblem.getOrder());
            }

            order = new Order(subOrders);
        } else {

            /**
             * once partioning is done we initialize an empty selection of
             * MenuItems (cost=0) and start iterating recursively to add
             * MenuItems
             */
            Map<MenuItem, Integer> current = new HashMap<>();
            nextProduct(current, 0.0, requirements);
        }

    }

    /**
     * tries to add items to the current selection, so that the selection
     * satisfies requirements and is of a smaller cost than currentCost
     *
     * @param current
     * @param currentCost
     * @param requirements
     */
    private void nextProduct(Map<MenuItem, Integer> current, double currentCost, Requirements requirements) {

        //handle the case where we've already seen this selection before
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);

        //iterate over the dishes in the requirements
        for (Map.Entry<Dish, Integer> requirement : requirements.getRequirements().entrySet()) {
            Dish dish = requirement.getKey();
            Integer quantityRequired = requirement.getValue();

            //we iterate over the menu, starting with the menu giving us the best offer for this dish
            List<MenuItem> sortedMenu = getSortedMenu(dish);
            for (MenuItem item : sortedMenu) {

                //if this menuItem contains this dish 
                //and current selection does not already contain this item
                // and adding 1xitem price is not more expensive than the current minimum cost
                if (item.getItems().containsKey(dish) && (!current.containsKey(item)) && item.getPrice() + currentCost < minimumCost) {
                    //figure out the max number of times we need to order this menuItem to satisfy this dish requirement
                    int supplied = item.getItems().get(dish);
                    int itemMultiplier = (int) Math.ceil((double) (quantityRequired) / supplied);
                    int maxJ = itemMultiplier;
                    if (minimumCost != Double.MAX_VALUE) {
                        maxJ = (int) ((minimumCost - currentCost) / item.getPrice());
                    }
                    //try reducing the quantity of this menuItem starting from max
                    for (int j = maxJ; j >0; j--) {
                        double nextCost = currentCost + item.getPrice() * j;
                        if (nextCost <= minimumCost) {
                            Map<MenuItem, Integer> next = new HashMap<>();
                            next.putAll(current);
                            next.put(item, j);

                            Requirements reducedRequirements = new RequirementsBuilder()
                                    .clone(requirements)
                                    .reduceRequirement(item, j)
                                    .build();
                            if (!reducedRequirements.getRequirements().isEmpty()) {
                                //if we still have further requirements, call recursively 
                                nextProduct(next, nextCost, reducedRequirements);
                            } else {
                                // if all requirements have been statisfied by this selection,
                                // check if it's better than the current best
                                double totalPrice = calculatePrice(next);
                                if (totalPrice < minimumCost) {
                                    minimumCost = totalPrice;
                                    System.out.println("current best price = " + minimumCost + " for " + next);
                                    order = new Order(next);
                                }
                            }
                        }
                    }

                }

            }
        }

    }

    public Order getOrder() {
        return order;
    }

    /**
     * sort the menu by dish cheapness
     * @param dish
     * @return 
     */
    private List<MenuItem> getSortedMenu(Dish dish) {

        if (menusSortedByDish.containsKey(dish)) {
            return menusSortedByDish.get(dish);
        }

        List<MenuItem> sorted = menu.stream().sorted((MenuItem o1, MenuItem o2) -> {
            boolean o1ContainsDish = o1.getItems().containsKey(dish);
            boolean o2ContainsDish = o2.getItems().containsKey(dish);
            if (!o1ContainsDish && !o2ContainsDish) {
                return 0;
            }
            if (o1ContainsDish && !o2ContainsDish) {
                return -1;
            }
            if (!o1ContainsDish && o2ContainsDish) {
                return 1;
            }
            return o2.dishCheapness(dish).compareTo(o1.dishCheapness(dish));
        }).collect(Collectors.toList());
        menusSortedByDish.put(dish, sorted);
        return sorted;
    }

}
