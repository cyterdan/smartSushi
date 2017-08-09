
package xyz.cyterdan.smartsushi.model;

/**
 * Used to build an instance of MenuItem 
 * @author cytermann
 */
public class MenuItemBuilder {
    
    private final MenuItem menuItem;

    public MenuItemBuilder() {
        menuItem  = new MenuItem();
    }
    
    public MenuItemBuilder name(String name){
        menuItem.setId(name);
        return this;
    }
    public MenuItemBuilder price(double price){
        menuItem.setPrice(price);
        return this;
    }
    
    public MenuItemBuilder addDish(Dish dish, int Quantity){
        menuItem.getItems().put(dish, Quantity);
        return this;
    }
    
    public MenuItem build() {
        if(menuItem.getItems().isEmpty()){
            throw new IllegalStateException("menuItem cannot be empty");
        }
        if(menuItem.getPrice()==null){
            throw new IllegalStateException("price cannot be null");
        }
        return menuItem;
    }
    
    
    
}
