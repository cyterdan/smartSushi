package xyz.cyterdan.smartsushi.model;

import xyz.cyterdan.smartsushi.model.Dish;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cytermann
 */
public class Requirements {

   private final Map<Dish, Integer> requirements;

    public Requirements() {
        requirements = new HashMap<>();
    }

    public Requirements(Requirements requirements) {
        this.requirements = new HashMap<>(requirements.getRequirements());
    }

    
    public Map<Dish, Integer> getRequirements() {
        return requirements;
    }
    
   

}
