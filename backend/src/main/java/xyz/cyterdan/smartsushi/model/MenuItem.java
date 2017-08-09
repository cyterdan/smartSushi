package xyz.cyterdan.smartsushi.model;

import xyz.cyterdan.smartsushi.model.Dish;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cytermann
 */
public class MenuItem {

    private String id;

    private final Map<Dish, Integer> items;

    private Double price;

    public MenuItem() {
        this.items = new HashMap<>();
    }

    public Map<Dish, Integer> getItems() {
        return items;
    }

    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    
    
    
    
    
    public MenuItem(String id, Map<Dish, Integer> items, double price) {
        this.id = id;
        this.items = items;
        this.price = price;
    }

    /**
     * returns the price cheapness (qty/$) of this menu item for a dish
     * @param dish
     * @return 
     */
    public Double dishCheapness(Dish dish) {
        int quantity = items.get(dish);
        return quantity/price;
    }

}
