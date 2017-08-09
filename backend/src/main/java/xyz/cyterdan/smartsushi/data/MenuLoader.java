package xyz.cyterdan.smartsushi.data;

import xyz.cyterdan.smartsushi.model.MenuItem;
import xyz.cyterdan.smartsushi.model.Dish;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author cytermann
 */
public interface MenuLoader {

    public Set<MenuItem> load() ;

    public Map<String, Dish> getDishes();

}
