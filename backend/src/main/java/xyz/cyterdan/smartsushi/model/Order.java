package xyz.cyterdan.smartsushi.model;
import xyz.cyterdan.smartsushi.model.Dish;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cytermann
 */
public class Order {

    Map<MenuItem, Integer> order;

    public Order(Map<MenuItem, Integer> order) {
        this.order = new HashMap<>();
        this.order.putAll(order);
    }

    public Order(List<Order> subOrders) {
        this.order = new HashMap<>();
        for(Order o : subOrders){
            order.putAll(o.getOrder());
        }
    }

    /**
     * return the total cost of this menu
     * @return 
     */
    public Double calculateCost() {
        double sum = 0;
        for (Map.Entry<MenuItem, Integer> entry : order.entrySet()) {
            sum += entry.getKey().getPrice() * entry.getValue();
        }
        return sum;

    }

    public Map<MenuItem, Integer> getOrder() {
        return order;
    }

    /**
     * Returns the item that are not in the requirements but are in the order
     * @param requirements
     * @return 
     */
    public Map<Dish,Integer> getBonus(Requirements requirements) {
        Map<Dish,Integer> orderedDishes = new HashMap<>();
         for (Map.Entry<MenuItem, Integer> entry : order.entrySet()) {
             for(Dish  dish: entry.getKey().getItems().keySet()){
                 int quantity = entry.getValue()*entry.getKey().getItems().get(dish);
                 if(!orderedDishes.containsKey(dish)){
                     orderedDishes.put(dish, quantity);
                 }
                 else{
                     int currentQuantity = orderedDishes.get(dish);
                     orderedDishes.put(dish, quantity+currentQuantity);
                 }
             }
        }
         
        for(Dish dish : requirements.getRequirements().keySet()){
            orderedDishes.put(dish, orderedDishes.get(dish)-requirements.getRequirements().get(dish));
            if(orderedDishes.get(dish)==0)
            {
                orderedDishes.remove(dish);
            }
        }
        return orderedDishes;
         
    }

}
