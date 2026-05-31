import javax.swing.*;
import java.io.*;

/**
 * This class simplifies the code in MarketDriver.java by handling most of the logic
 * Instead of writing a large block of code, make it a method here and call it
 */

public class MarketPlace implements Serializable {
    private static final int PASSWORD_LENGTH = 1;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public MarketPlace(ObjectOutputStream oos, ObjectInputStream ois) {
        this.ois = ois;
        this.oos = oos;
    }

    public String read() {
        try {
            return String.valueOf(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return "";
        }
    }

    public void write(String message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            return;
        }
    }


    public boolean createAccount(String username, String password, boolean sellerSelected, boolean customerSelected) {
        try {
            if (!sellerSelected && !customerSelected) {
                JOptionPane.showMessageDialog(null,
                        "Must select buyer or seller", "Empty User Type", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Must enter a username", "Empty Username", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Must enter a password", "Empty Password", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (password.length() < PASSWORD_LENGTH) {
                JOptionPane.showMessageDialog(null,
                        "Password must be at least 8 characters long", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                write(String.format("checkUsernameExists,%s", username));
                if (read().equals("true")) {
                    JOptionPane.showMessageDialog(null,
                            "Username is already taken", "Username Taken", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if (sellerSelected) {
                write(String.format("createSellerAccount,%s,%s", username, password));
            } else {
                write(String.format("createCustomerAccount,%s,%s", username, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String login(String username, String password, JTextField usernameField, JTextField passwordField) {
        try {
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Must enter a username", "Empty Username", JOptionPane.ERROR_MESSAGE);
                return "";
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Must enter a password", "Empty Password", JOptionPane.ERROR_MESSAGE);
                return "";
            } else {
                write(String.format("checkUsernameExists,%s", username));

                if (read().equals("false")) {
                    JOptionPane.showMessageDialog(null,
                            "No account matches that username", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                    return "";
                }

                write(String.format("getUserPassword,%s", username));
                String userPassword = read();
                if (!password.equals(userPassword)) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid Password", "Invalid Credentials", JOptionPane.ERROR_MESSAGE);
                    return "";
                }
            }
            write(String.format("setUser,%s", username));
            return getUserType();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean changeUsername(String username, String confirm, JTextField usernameField, JTextField confirmField) {
        if (!username.equals(confirm)) {
            JOptionPane.showMessageDialog(null,
                    "Usernames do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            try {
                write(String.format("checkUsernameExists,%s", username));
                if (read().equals("true")) {
                    JOptionPane.showMessageDialog(null,
                            "Username already exists", "Username taken", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    confirmField.setText("");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        write(String.format("changeUsername,%s", username));
        JOptionPane.showMessageDialog(null,
                "Successfully changed username", "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean changePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null,
                    "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (password.length() < PASSWORD_LENGTH) {
            JOptionPane.showMessageDialog(null,
                    "Password must be at least 8 characters long", "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("changePassword,%s", password));
        JOptionPane.showMessageDialog(null,
                "Successfully changed password", "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public void deleteAccount() {
        write("deleteAccount");
    }

    public String getStores() {
        write("getStoreList");

        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getUserType() {
        write("getUserType");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean addStore(String storeName) {
        if (storeName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Store name cannot be empty", "Invalid Store Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        write(String.format("checkStoreNameTaken,%s", storeName));
        try {
            if (read().equals("true")) {
                JOptionPane.showMessageDialog(null,
                        "Store name is already taken", "Store Name Taken", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        write(String.format("addStore,%s", storeName));
        return true;
    }

    public void deleteStore() {
        write(String.format("removeStore"));
        JOptionPane.showMessageDialog(null,
                "Successfully removed store", "Store Removed", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean changeStoreName(String storeName, String confirmStoreName) {
        if (storeName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Store name cannot be empty", "Empty Store Name", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (!storeName.equals(confirmStoreName)) {
            JOptionPane.showMessageDialog(null,
                    "Store name does not match", "Names Not Matching", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("checkStoreNameTaken,%s,", storeName));
        try {
            if (read().equals("true")) {
                JOptionPane.showMessageDialog(null,
                        "Name is already taken", "Name Taken", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        write(String.format("changeStoreName,%s", storeName));
        JOptionPane.showMessageDialog(null,
                "Successfully changed store name", "Store Name Changed", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean addProduct(String name, String description, String quantity, String price) {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Product name cannot be empty", "Empty Product Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Product description cannot be empty", "Empty Product Description", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int productQuantity;
        double productPrice;

        try {
            productQuantity = Integer.parseInt(quantity);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Enter a integer greater than 0 for product quantity", "Invalid Product Quantity",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (productQuantity <= 0) {
            JOptionPane.showMessageDialog(null,
                    "Product quantity has to be greater than 0",
                    "Invalid Product Quantity", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            productPrice = Double.parseDouble(price);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Enter a double greater than 0 for product price",
                    "Invalid Product Price", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (productPrice <= 0) {
            JOptionPane.showMessageDialog(null,
                    "Product price has to be greater than 0",
                    "Invalid Product Price", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("addProduct,%s,%s,%s,%s", name, description, productQuantity, productPrice));
        JOptionPane.showMessageDialog(null,
                "Successfully added product to store",
                "Product Added", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean importProducts(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null,
                        "File does not exist",
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String lineToRead = reader.readLine();
            System.out.println(lineToRead);
            while (lineToRead != null) {
                String[] productInfo = lineToRead.split(",");
                write(String.format("addProduct,%s,%s,%s,%s", productInfo[0],
                        productInfo[1], productInfo[2], (productInfo[3])));
                lineToRead = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "There was an error importing from the file",
                    "Error importing", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JOptionPane.showMessageDialog(null,
                "Successfully imported products from file",
                "Successfully imported", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public void exportProducts() {
        write("getProductList");

        try {
            if (read().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "This store has no products to export", "No Products", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        write("exportProducts");

        try {
            JOptionPane.showMessageDialog(null, "Successfully exported products to file: " + read(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean changeProductName(String name) {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Product name cannot be empty", "Empty Product Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("changeProductName,%s", name));
        JOptionPane.showMessageDialog(null,
                "Successfully changed product name", "Name Changed", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean changeProductDescription(String description) {
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Product description cannot be empty",
                    "Empty Product Description", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("changeProductDescription,%s", description));
        JOptionPane.showMessageDialog(null, "Successfully changed product description",
                "Description Changed", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean changeProductQuantity(String quantity) {
        int productQuantity;

        try {
            productQuantity = Integer.parseInt(quantity);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Enter a integer greater than 0 for product quantity",
                    "Invalid Product Quantity", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (productQuantity <= 0) {
            JOptionPane.showMessageDialog(null, "Product quantity has to be greater than 0",
                    "Invalid Product Quantity", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("changeProductQuantity,%s", quantity));
        JOptionPane.showMessageDialog(null, "Successfully changed product quantity",
                "Quantity Changed", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean changeProductPrice(String price) {
        double productPrice;
        try {
            productPrice = Double.parseDouble(price);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Enter a double greater than 0 for product price",
                    "Invalid Product Price", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (productPrice <= 0) {
            JOptionPane.showMessageDialog(null, "Product price has to be greater than 0",
                    "Invalid Product Price", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        write(String.format("changeProductPrice,%s", price));
        JOptionPane.showMessageDialog(null, "Successfully changed product price",
                "Price Changed", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public void deleteProduct() {
        write("deleteProduct");
        JOptionPane.showMessageDialog(null, "Successfully removed product from store",
                "Product Removed", JOptionPane.INFORMATION_MESSAGE);
    }


    public String getStoreCustomerStats() {
        write("getStoreCustomerStats");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getProductStats() {
        write("getProductStats");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCustomerCarts() {
        write("getCustomerCarts");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAllItems() {
        write("getAllItems");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public void addItemToCart(int quantity) {
        write(String.format("addItemToCart,%d", quantity));
        if (read().equals("-1")) {
            JOptionPane.showMessageDialog(null, "Item was out of stock",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Successfully added item to cart",
                "Item Added to Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    public String getCart() {
        write("getCart");
        try {
            return read();
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemsByCategory(String searchChoice, String term) {
        if (searchChoice.isEmpty() && term.isEmpty()) {
            return getAllItems();
        }

        write(String.format("getItemsByCategory,%s,%s", searchChoice, term));
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getCustomerShoppingHistory() {
        write("getPurchasedList");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void removeItemFromCart(String productName, int productQuantity) {
        write(String.format("removeItemFromCart,%s,%d", productName, productQuantity));
        JOptionPane.showMessageDialog(null, "Successfully removed item from cart",
                "Item Removed", JOptionPane.INFORMATION_MESSAGE);
    }

    public void purchaseCart() {
        write("purchaseCart");
        JOptionPane.showMessageDialog(null, "Successfully purchased all items in cart",
                "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    public void exportPurchaseHistory() {
        write("getPurchasedList");
        String purchasedList = "";
        try {
            purchasedList = read();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (purchasedList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have not made any purchases",
                    "Empty Purchase History", JOptionPane.ERROR_MESSAGE);
            return;
        }

        write("exportPurchaseHistory");
        try {
            JOptionPane.showMessageDialog(null, "Successfully exported purchase history to file: " + read(),
                    "Successfully Exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPurchasedList() {
        write("getPurchasedList");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCustomerStatisticsByStore() {
        write("getCustomerStatisticsByStore");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCustomerStatisticsByProduct() {
        write("getCustomerStatisticsByProduct");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        write("getUsername");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setStore(String storeName) {
        write(String.format("setStore,%s", storeName));
    }

    public String getStoreName() {
        write("getStoreName");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProductList() {
        write("getProductList");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setProduct(String productName, String storeName, String sellerName) {
        write(String.format("setProduct,%s,%s,%s", productName, storeName, sellerName));
    }

    public String getSalesNamesList() {
        write("getSalesNamesList");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSalesList() {
        write("getSales");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProductName() {
        write("getProductName");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProductDescription() {
        write("getProductDescription");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProductQuanity() {
        write("getProductQuantity");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProductPrice() {
        write("getProductPrice");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getProduct() {
        write("getProduct");
        try {
            return read();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
