
package xyz.cyterdan.smartsushi.api;

import java.util.Map;
import xyz.cyterdan.smartsushi.model.Dish;
import xyz.cyterdan.smartsushi.model.Requirements;
import xyz.cyterdan.smartsushi.model.RequirementsBuilder;

/**
 *
 * @author cytermann
 */
public class Utils {
     public static Requirements fromHtmlParam(Map<String, Object> posted) {
         RequirementsBuilder builder = new RequirementsBuilder();
         
         for(String key : posted.keySet()){
             if(key.startsWith("requirements[")){
                 String dishName = key.replace("requirements[","").replace("]","");
                 Integer quantity = Integer.valueOf(posted.get(key).toString());
                 builder.addRequirement(new Dish(dishName), quantity);
             }
         }
         return builder.build();
    }
}
