import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for managing and manipulating all the data associated with sellers.
 * Sellers have attributes of parent user: name, password and userType "seller";
 * The only other thing is an arraylist of stores which the seller manages.
 */

public class Seller extends User implements Serializable {
    private ArrayList<Store> storeList;

    public Seller(String username, String password, String userType) {
        super(username, password, userType);
        storeList = new ArrayList<>();
    }

    public ArrayList<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(ArrayList<Store> storeList) {
        this.storeList = storeList;
    }

    public void printStores() {
        for (Store s : storeList) {
            System.out.println(s.getName());
        }
    }

    public int findStore(String name) {
        for (int i = 0; i < storeList.size(); i++) {
            Store p = storeList.get(i);
            if (p.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}