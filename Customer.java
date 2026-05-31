import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for managing and manipulating all the data associated with customers
 */

public class Customer extends User implements Serializable {
    private ArrayList<Product> cart;
    private ArrayList<Product> purchasedList;

    public Customer(String username, String password, String userType) {
        super(username, password, userType);
        cart = new ArrayList<>();
        purchasedList = new ArrayList<>();
    }

    public ArrayList<Product> getCart() {
        return cart;
    }

    public ArrayList<Product> getPurchasedList() {
        return purchasedList;
    }

    synchronized public void addItemToCart(Product product) {
        cart.add(product);
    }

    synchronized public void buyAllItems(String buyerName) {
        for (int i = 0; i < cart.size(); i++) {
            if (buyItem(cart.get(i), buyerName)) {
                i--;
            }
        }
    }

    public boolean buyItem(Product product, String buyerName) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getName().equals(product.getName())) {
                purchasedList.add(cart.get(i));
                product.getStore().getSalesList().add(product);
                product.getStore().getSalesNamesList().add(buyerName);
                cart.remove(i);
                return true;
            }
        }
        return false;
    }
}