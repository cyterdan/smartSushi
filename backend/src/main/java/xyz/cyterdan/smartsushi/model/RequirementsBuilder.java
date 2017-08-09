package xyz.cyterdan.smartsushi.model;

import xyz.cyterdan.smartsushi.model.Dish;
import java.util.Map.Entry;

/**
 * Constructs a Requirements object
 * @author cytermann
 */
public class RequirementsBuilder {

    private final Requirements requirements;

    public RequirementsBuilder() {
        requirements = new Requirements();
    }

    public RequirementsBuilder addRequirement(Dish dish, Integer quantity) {
        requirements.getRequirements().put(dish, quantity);
        return this;
    }
    
    public RequirementsBuilder clone(Requirements other){
        requirements.getRequirements().clear();
        requirements.getRequirements().putAll(other.getRequirements());
        return this;
    }
    
    public RequirementsBuilder removeRequirement(Dish dish){
        requirements.getRequirements().remove(dish);
        return this;
    }
    
    public Requirements build(){
        return requirements;
    }

    /**
     * remove quantity from menu item
     * @param item
     * @param quantity
     * @return 
     */
    public RequirementsBuilder reduceRequirement(MenuItem item, int quantity) {
            for(Entry<Dish,Integer> entry : item.getItems().entrySet()){
                
                Dish toRemove = entry.getKey();
                int removeQuantity = entry.getValue()*quantity;
                
                if(requirements.getRequirements().containsKey(toRemove)){
                    int requiredQuantity = requirements.getRequirements().get(toRemove);
                    if(requiredQuantity<=removeQuantity){
                        requirements.getRequirements().remove(toRemove);
                    }
                    else{
                        requirements.getRequirements().put(toRemove, requiredQuantity-removeQuantity);
                    }
                }
                
            }
            return this;
        
    }

}
