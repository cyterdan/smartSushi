
package xyz.cyterdan.smartsushi.model;

/**
 *
 * @author cytermann
 */
public class Price {
    
    double price;

    public Price(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return price+"";
    }
    
    
    
}
