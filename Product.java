import java.io.Serializable;
import java.util.Objects;

/**
 * Class for managing and manipulating all the data associated with products
 * Sellers can add products to their stores which customers can then view and purchase
 */

public class Product implements Serializable {
    private Seller seller;
    private Store store;
    private String name;
    private String description;
    private int quantity;
    private double price;

    public Product(Seller seller, Store store, String name, String description, int quantity, double price) {
        this.seller = seller;
        this.store = store;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public Seller getSeller() {
        return seller;
    }

    public Store getStore() {
        return store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    synchronized public void removeQuantity(int quantityToRemove) {
        this.quantity -= quantityToRemove;
    }

    public void addQuantity(int quantityToAdd) {
        this.quantity += quantityToAdd;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String list() {
        return String.format("Name: %s ($%.2f)", name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return quantity == product.quantity && Double.compare(price, product.price) == 0
                && seller.getUsername().equals(product.getSeller().getUsername())
                && store.getName().equals(product.getStore().getName()) && name.equals(product.getName())
                && description.equals(product.getDescription());
    }

    @Override
    public String toString() {
        return String.format("%s | Quantity: %d | Description: %s", list(), quantity, description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seller, store, name, description, quantity, price);
    }
}