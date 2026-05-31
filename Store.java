import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for managing and manipulating all the data associated with stores.
 * Managed by a seller and has an arraylist of products.
 */

public class Store implements Serializable {
    private String name;
    private ArrayList<Product> productList;

    private ArrayList<Product> salesList;
    private ArrayList<String> salesNamesList;

    public Store(String name) {
        this.name = name;
        productList = new ArrayList<>();
        salesList = new ArrayList<>();
        salesNamesList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public ArrayList<Product> getSalesList() {
        return salesList;
    }

    public void setSalesList(ArrayList<Product> salesList) {
        this.salesList = salesList;
    }

    public ArrayList<String> getSalesNamesList() {
        return salesNamesList;
    }

    public void setSalesNamesList(ArrayList<String> salesNamesList) {
        this.salesNamesList = salesNamesList;
    }

    public int findProduct(String nameToFind) {
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            if (p.getName().equals(nameToFind)) {
                return i;
            }
        }
        return -1;
    }

    public void printProducts() {
        for (Product p : productList) {
            System.out.println(p);
        }
    }

    public void editProduct(String nameToFind, String newValue, String editParam) {
        int idx = findProduct(nameToFind);
        try {
            switch (editParam) {
                case "name":
                    productList.get(idx).setName(newValue);
                    break;
                case "description":
                    productList.get(idx).setDescription(newValue);
                    break;
                case "quantity":
                    productList.get(idx).setQuantity(Integer.parseInt(newValue));
                    break;
                case "price":
                    productList.get(idx).setPrice(Double.parseDouble(newValue));
                    break;
            }
        } catch (NumberFormatException nfe) {
            return;
        }
    }

    public void removeProduct(Product product) {
        productList.remove(product);
    }
}
