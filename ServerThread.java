import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Instantiated and started everytime a new client connects.
 * Reads in commands from client, checks if possible and executes on maps of users stored in server.
 */

public class ServerThread extends Thread {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    Socket socket;
    public static Object obj = new Object(); // Used for synchronization of code

    ServerThread(Socket socket) {
        this.socket = socket;
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

    @Override
    public void run() {
        User selectedUser = null;
        Store selectedStore = null;
        Product selectedProduct = null;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    String userInput = read();
                    if (userInput.isEmpty()) {
                        continue;
                    }
                    System.out.println("Received the following request from user: " + userInput);
                    String[] input = userInput.split(",");

                    String userRequest = input[0];
                    switch (userRequest) {
                        case "createSellerAccount":
                            Seller sellerToAdd = new Seller(input[1], input[2], "seller");
                            synchronized (obj) {
                                Server.sellerMap.put(input[1], sellerToAdd);
                            }
                            selectedUser = sellerToAdd;
                            break;
                        case "createCustomerAccount":
                            Customer customerToAdd = new Customer(input[1], input[2], "customer");
                            synchronized (obj) {
                                Server.customerMap.put(input[1], customerToAdd);
                            }
                            selectedUser = customerToAdd;
                            break;
                        case "setUser":
                            String nameOfUserToSetAsActive = input[1];
                            User userToSetAsActive;
                            synchronized (obj) {
                                userToSetAsActive = Server.customerMap.get(nameOfUserToSetAsActive);
                            }
                            if (userToSetAsActive == null) {
                                synchronized (obj) {
                                    userToSetAsActive = Server.sellerMap.get(nameOfUserToSetAsActive);
                                }
                            }
                            selectedUser = userToSetAsActive;
                            break;
                        case "setProduct":
                            synchronized (obj) {
                                Seller s = Server.sellerMap.get(input[3]);
                                Store storeThatHasProduct = s.getStoreList().get(s.findStore(input[2]));
                                for (Product product : storeThatHasProduct.getProductList()) {
                                    if (product.getName().equals(input[1])) {
                                        selectedProduct = product;
                                        break;
                                    }
                                }
                            }
                            break;
                        case "getUserPassword":
                            String nameOfUserToGetPassword = input[1];
                            User userToGetPasswordFrom;
                            synchronized (obj) {
                                userToGetPasswordFrom = Server.customerMap.get(nameOfUserToGetPassword);
                            }
                            if (userToGetPasswordFrom == null) {
                                synchronized (obj) {
                                    userToGetPasswordFrom = Server.sellerMap.get(nameOfUserToGetPassword);
                                }
                            }
                            write(userToGetPasswordFrom.getPassword());
                            break;
                        case "getUsername":
                            write(selectedUser.getUsername());
                            break;
                        case "getStoreList":
                            String listOfStoreNames = "";
                            synchronized (obj) {
                                for (Store storeToGetName : ((Seller) selectedUser).getStoreList()) {
                                    listOfStoreNames += storeToGetName.getName() + ",";
                                }
                            }
                            write(listOfStoreNames);
                            break;
                        case "hasStores":
                            boolean isStoreListEmpty;
                            synchronized (obj) {
                                isStoreListEmpty = ((Seller) selectedUser).getStoreList().isEmpty();
                            }
                            if (isStoreListEmpty) {
                                write("false");
                            } else {
                                write("true");
                            }
                            break;
                        case "getUserType":
                            boolean selectedUserEqualsSeller;
                            synchronized (obj) {
                                selectedUserEqualsSeller = selectedUser.getUserType().equals("seller");
                            }
                            if (selectedUserEqualsSeller) {
                                write("seller");
                            } else {
                                write("customer");
                            }
                            break;
                        case "getStoreCustomerStats":
                            HashMap<String, Integer> storeCustomerStatsMap = new HashMap<>();

                            synchronized (obj) {
                                ArrayList<Product> products = selectedStore.getSalesList();
                                ArrayList<String> names = selectedStore.getSalesNamesList();
                                for (int i = 0; i < names.size(); i++) {
                                    if (storeCustomerStatsMap.containsKey(names.get(i))) {
                                        storeCustomerStatsMap.put(names.get(i),
                                                storeCustomerStatsMap.get(names.get(i))
                                                        + products.get(i).getQuantity());
                                    } else {
                                        storeCustomerStatsMap.put(names.get(i), products.get(i).getQuantity());
                                    }
                                }
                            }

                            String storeCustomerStats = "";
                            for (String nameOfStoreCustomer : storeCustomerStatsMap.keySet()) {
                                storeCustomerStats += nameOfStoreCustomer
                                        + "," + storeCustomerStatsMap.get(nameOfStoreCustomer) + "~";
                            }
                            write(storeCustomerStats);
                            break;
                        case "getProductStats":
                            HashMap<String, Integer> productsStatsMap = new HashMap<>();

                            synchronized (obj) {
                                ArrayList<Product> productsStatsList = selectedStore.getSalesList();
                                for (int i = 0; i < productsStatsList.size(); i++) {
                                    if (productsStatsMap.containsKey(productsStatsList.get(i).getName())) {
                                        productsStatsMap.put(productsStatsList.get(i).getName(),
                                                productsStatsMap.get(productsStatsList.get(i).getName())
                                                        + productsStatsList.get(i).getQuantity());
                                    } else {
                                        productsStatsMap.put(productsStatsList.get(i).getName(),
                                                productsStatsList.get(i).getQuantity());
                                    }
                                }
                            }

                            String productsStats = "";
                            for (String name : productsStatsMap.keySet()) {
                                productsStats += name + "," + productsStatsMap.get(name) + "~";
                            }
                            write(productsStats);
                            break;
                        case "getCustomerCarts":
                            HashMap<String, Integer> customerCartsMap = new HashMap<>();

                            synchronized (obj) {
                                for (Map.Entry<String, Customer> entry : Server.customerMap.entrySet()) {
                                    for (Product p : entry.getValue().getCart()) {
                                        if (p.getStore().getName().equals(selectedStore.getName())) {
                                            if (customerCartsMap.containsKey(p.getName())) {
                                                customerCartsMap.put(p.getName(),
                                                        customerCartsMap.get(p.getName()) + p.getQuantity());
                                            } else {
                                                customerCartsMap.put(p.getName(), p.getQuantity());
                                            }
                                        }
                                    }
                                }
                            }

                            String listOfCustomerCarts = "";
                            for (String customerNameToGetCart : customerCartsMap.keySet()) {
                                listOfCustomerCarts += customerNameToGetCart
                                        + "," + customerCartsMap.get(customerNameToGetCart) + "~";
                            }
                            write(listOfCustomerCarts);
                            break;
                        case "getCart":
                            String stringValueOfCart = "";

                            synchronized (obj) {
                                ArrayList<Product> customerCart = ((Customer) selectedUser).getCart();
                                for (Product productInCart : customerCart) {
                                    stringValueOfCart += writeProductToString(productInCart) + "~";
                                }
                            }

                            write(stringValueOfCart);
                            break;
                        case "getItemsByCategory":
                            String searchChoice = input[1];
                            String term = input[2];

                            ArrayList<Product> all = new ArrayList<>();
                            if (searchChoice.equals("Product Name")) {
                                synchronized (obj) {
                                    for (Map.Entry<String, Seller> entry : Server.sellerMap.entrySet()) {
                                        for (Store s : entry.getValue().getStoreList()) {
                                            for (Product p : s.getProductList()) {
                                                if (p.getQuantity() > 0 &&
                                                        p.getName().toLowerCase().contains(term.toLowerCase())) {
                                                    all.add(p);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (searchChoice.equals("Product Description")) {
                                synchronized (obj) {
                                    for (Map.Entry<String, Seller> entry : Server.sellerMap.entrySet()) {
                                        for (Store s : entry.getValue().getStoreList()) {
                                            for (Product p : s.getProductList()) {
                                                if (p.getQuantity() > 0 &&
                                                    p.getDescription().toLowerCase().contains(term.toLowerCase())) {
                                                    all.add(p);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (searchChoice.equals("Store")) {
                                synchronized (obj) {
                                    for (Map.Entry<String, Seller> entry : Server.sellerMap.entrySet()) {
                                        for (Store s : entry.getValue().getStoreList()) {
                                            for (Product p : s.getProductList()) {
                                                if (p.getQuantity() > 0 &&
                                                p.getStore().getName().toLowerCase().contains(term.toLowerCase())) {
                                                    all.add(p);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            String itemsInCategory = "";
                            for (Product productToLoop : all) {
                                itemsInCategory += writeProductToString(productToLoop) + "~";
                            }
                            write(itemsInCategory);
                            break;
                        case "getCustomerStatisticsByStore":
                            HashMap<String, Integer> map = new HashMap<>();

                            synchronized (obj) {
                                for (Product p : ((Customer) selectedUser).getPurchasedList()) {
                                    if (map.containsKey(p.getStore().getName())) {
                                        map.put(p.getStore().getName(),
                                                map.get(p.getStore().getName()) + p.getQuantity());
                                    } else {
                                        map.put(p.getStore().getName(), p.getQuantity());
                                    }
                                }
                            }

                            String stringCustomerStatisticsByStore = "";
                            for (String productStoreName : map.keySet()) {
                                stringCustomerStatisticsByStore += String.format("%s/%d~", productStoreName,
                                        map.get(productStoreName));
                            }
                            write(stringCustomerStatisticsByStore);
                            break;
                        case "getCustomerStatisticsByProduct":
                            HashMap<Product, Integer> byProd = new HashMap<>();

                            synchronized (obj) {
                                for (Product p : ((Customer) selectedUser).getPurchasedList()) {
                                    Product temp = new Product(p.getSeller(), p.getStore(),
                                            p.getName(), p.getDescription(), 0, p.getPrice());
                                    if (byProd.containsKey(temp)) {
                                        byProd.put(temp, byProd.get(temp) + p.getQuantity());
                                    } else {
                                        byProd.put(temp, p.getQuantity());
                                    }
                                }
                            }

                            String stringCustomerStatisticsByProduct = "";
                            for (Product productInMap : byProd.keySet()) {
                                stringCustomerStatisticsByProduct += writeProductToString(productInMap)
                                        + "/" + byProd.get(productInMap) + "~";
                            }
                            write(stringCustomerStatisticsByProduct);
                            break;
                        case "getAllItems":
                            ArrayList<Product> allItems = new ArrayList<>();

                            synchronized (obj) {
                                for (Map.Entry<String, Seller> entry : Server.sellerMap.entrySet()) {
                                    for (Store s : entry.getValue().getStoreList()) {
                                        for (Product p : s.getProductList()) {
                                            if (p.getQuantity() > 0) {
                                                Product temp = new Product(p.getSeller(), p.getStore(),
                                                    p.getName(), p.getDescription(), p.getQuantity(), p.getPrice());
                                                allItems.add(temp);
                                            }
                                        }
                                    }
                                }
                            }

                            String allItemString = "";
                            for (Product allItemProduct : allItems) {
                                allItemString += writeProductToString(allItemProduct) + "~";
                            }
                            write(allItemString);
                            break;
                        case "getProductList":
                            String stringStoreProducts = "";

                            synchronized (obj) {
                                ArrayList<Product> storeProductList = selectedStore.getProductList();
                                for (Product productInStore : storeProductList) {
                                    stringStoreProducts += writeProductToString(productInStore) + "~";
                                }
                            }

                            write(stringStoreProducts);
                            break;
                        case "checkStoreNameTaken":
                            String storeNameToCheck = input[1];
                            boolean storeNameTaken = false;

                            synchronized (obj) {
                                for (Store storesToCheck : ((Seller) selectedUser).getStoreList()) {
                                    if (storesToCheck.getName().equals(storeNameToCheck)) {
                                        storeNameTaken = true;
                                        break;
                                    }
                                }
                            }

                            if (storeNameTaken) {
                                write("true");
                            } else {
                                write("false");
                            }
                            break;
                        case "checkUsernameExists":
                            String usernameToCheck = input[1];
                            boolean usernameTaken = false;

                            synchronized (obj) {
                                for (String customerUsername : Server.customerMap.keySet()) {
                                    if (customerUsername.equals(usernameToCheck)) {
                                        usernameTaken = true;
                                        break;
                                    }
                                }
                                if (!usernameTaken) {
                                    for (String sellerUsername : Server.sellerMap.keySet()) {
                                        if (sellerUsername.equals(usernameToCheck)) {
                                            usernameTaken = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (usernameTaken) {
                                write("true");
                            } else {
                                write("false");
                            }
                            break;
                        case "removeStore":
                            synchronized (obj) {
                                ((Seller) selectedUser).getStoreList().remove(selectedStore);
                            }
                            break;
                        case "removeItemFromCart":
                            String productNameToRemove = input[1];
                            int quantityOfProduct = Integer.parseInt(input[2]);
                            Store storeOfProductToRemove = null;

                            synchronized (obj) {
                                ArrayList<Product> cartOfCustomerRequestingRemove =
                                        ((Customer) selectedUser).getCart();
                                for (int i = 0; i < cartOfCustomerRequestingRemove.size(); i++) {
                                    if (cartOfCustomerRequestingRemove.get(i).getName().equals(productNameToRemove)) {
                                        storeOfProductToRemove = cartOfCustomerRequestingRemove.get(i).getStore();
                                        cartOfCustomerRequestingRemove.remove(i);
                                    }
                                }

                                ArrayList<Product> list = storeOfProductToRemove.getProductList();
                                for (Product storeProduct : list) {
                                    if (storeProduct.getName().equals(productNameToRemove)) {
                                        storeProduct.addQuantity(quantityOfProduct);
                                    }
                                }
                            }
                            break;
                        case "addStore":
                            String storeNameToAdd = input[1];
                            synchronized (obj) {
                                ((Seller) selectedUser).getStoreList().add(new Store(storeNameToAdd));
                            }
                            break;
                        case "changeStoreName":
                            String newStoreName = input[1];
                            synchronized (obj) {
                                selectedStore.setName(newStoreName);
                            }
                            break;
                        case "addProduct":
                            synchronized (obj) {
                                selectedStore.getProductList().add(new Product((Seller) selectedUser,
                                        selectedStore, input[1], input[2], Integer.parseInt(input[3]),
                                        Double.parseDouble(input[4])));
                            }
                            break;
                        case "exportProducts":
                            File file;
                            synchronized (obj) {
                                ArrayList<Product> productsToExport = selectedStore.getProductList();
                                file = new File(selectedUser.getUsername() + "-" + selectedStore.getName() +
                                        "-products.csv");
                                int i = 2;
                                while (file.exists()) {
                                    file = new File(selectedUser.getUsername()
                                            + "-" + selectedStore.getName() + "-products" + "-" + i + ".csv");
                                    i++;
                                }
                                try {
                                    PrintWriter fileWriter = new PrintWriter(new FileOutputStream(file));
                                    for (Product product : productsToExport) {
                                        fileWriter.println(product.getName() + "," + product.getDescription()
                                                + "," + product.getQuantity() + "," +
                                                product.getPrice());
                                    }
                                    fileWriter.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            write(file.getName());
                            break;

                        case "changeProductName":
                            synchronized (obj) {
                                selectedProduct.setName(input[1]);
                            }
                            break;

                        case "changeProductDescription":
                            synchronized (obj) {
                                selectedProduct.setDescription(input[1]);
                            }
                            break;

                        case "changeProductQuantity":
                            synchronized (obj) {
                                selectedProduct.setQuantity(Integer.parseInt(input[1]));
                            }
                            break;

                        case "changeProductPrice":
                            synchronized (obj) {
                                selectedProduct.setPrice(Double.parseDouble(input[1]));
                            }
                            break;

                        case "deleteProduct":
                            synchronized (obj) {
                                selectedStore.removeProduct(selectedProduct);
                            }
                            break;

                        case "purchaseCart":
                            synchronized (obj) {
                                ((Customer) selectedUser).buyAllItems(selectedUser.getUsername());
                            }
                            break;

                        case "exportPurchaseHistory":
                            File file2;

                            synchronized (obj) {
                                ArrayList<Product> purchases = ((Customer) selectedUser).getPurchasedList();

                                file2 = new File(selectedUser.getUsername() + "-PurchaseHistory.csv");
                                int a = 2;
                                while (file2.exists()) {
                                    file2 = new File(selectedUser.getUsername() + "-PurchaseHistory-" + a + ".csv");
                                    a++;
                                }

                                try {
                                    PrintWriter writer = new PrintWriter(new FileOutputStream(file2));
                                    for (Product product : purchases) {
                                        writer.println(product.getName() + "," + product.getDescription() + "," +
                                                product.getQuantity() + "," + product.getPrice());
                                    }
                                    writer.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            write(file2.getName());
                            break;

                        case "changeUsername":
                            String newUsername = input[1];
                            synchronized (obj) {
                                String old = selectedUser.getUsername();
                                selectedUser.setUsername(newUsername);
                                if (selectedUser instanceof Customer) {
                                    Server.customerMap.remove(old);
                                    Server.customerMap.put(newUsername, (Customer) selectedUser);
                                }
                                if (selectedUser instanceof Seller) {
                                    Server.sellerMap.remove(old);
                                    Server.sellerMap.put(newUsername, (Seller) selectedUser);
                                }
                            }
                            break;

                        case "changePassword":
                            String newPassword = input[1];
                            synchronized (obj) {
                                selectedUser.setPassword(newPassword);
                            }
                            break;

                        case "deleteAccount":
                            synchronized (obj) {
                                if (selectedUser instanceof Customer) {
                                    for (Product fromCart : ((Customer) selectedUser).getCart()) {
                                        for (Product prod : fromCart.getStore().getProductList()) {
                                            if (fromCart.getName().equals(prod.getName())) {
                                                prod.addQuantity(fromCart.getQuantity());
                                            }
                                        }
                                    }
                                    Server.customerMap.remove(selectedUser.getUsername());
                                }
                                if (selectedUser instanceof Seller) {
                                    for (Store s : ((Seller) selectedUser).getStoreList()) {
                                        for (Product p : s.getProductList()) {
                                            removeProductFromCarts(p, Server.customerMap);
                                        }
                                    }
                                    Server.sellerMap.remove(selectedUser.getUsername());
                                }
                            }
                            break;
                        case "addItemToCart":
                            int quanityToAddToCart = Integer.parseInt(input[1]);
                            synchronized (obj) {
                                Customer customer = (Customer) selectedUser;
                                selectedProduct.removeQuantity(quanityToAddToCart);
                                if (selectedProduct.getQuantity() < 0) {
                                    write("-1");
                                    break;
                                }
                                write("1");
                                customer.addItemToCart(new Product(selectedProduct.getSeller(),
                                        selectedProduct.getStore(), selectedProduct.getName(),
                                        selectedProduct.getDescription(),
                                        quanityToAddToCart, selectedProduct.getPrice()));
                            }
                            break;
                        case "getPurchasedList":
                            String stringPurchasedList = "";
                            synchronized (obj) {
                                ArrayList<Product> purchasedList = ((Customer) selectedUser).getPurchasedList();
                                for (Product productInPurchasedList : purchasedList) {
                                    stringPurchasedList += writeProductToString(productInPurchasedList) + "~";
                                }
                            }

                            write(stringPurchasedList);
                            break;

                        case "setStore":
                            String storeNameToSet = input[1];
                            synchronized (obj) {
                                ArrayList<Store> storesToLoop = ((Seller) selectedUser).getStoreList();
                                for (Store storeToCheckName : storesToLoop) {
                                    if (storeToCheckName.getName().equals(storeNameToSet)) {
                                        selectedStore = storeToCheckName;
                                        break;
                                    }
                                }
                            }
                            break;

                        case "getStoreName":
                            synchronized (obj) {
                                write(selectedStore.getName());
                            }

                            break;

                        case "getSalesNamesList":
                            String listOfNames = "";
                            synchronized (obj) {
                                for (String name : selectedStore.getSalesNamesList()) {
                                    listOfNames += name + ",";
                                }
                            }
                            write(listOfNames);
                            break;

                        case "getSales":
                            String listOfSoldProducts = "";
                            synchronized (obj) {
                                for (Product product : selectedStore.getSalesList()) {
                                    listOfSoldProducts += writeProductToString(product) + "~";
                                }
                            }
                            write(listOfSoldProducts);
                            break;

                        case "getProductName":
                            synchronized (obj) {
                                write(selectedProduct.getName());
                            }
                            break;

                        case "getProductDescription":
                            synchronized (obj) {
                                write(selectedProduct.getDescription());
                            }
                            break;

                        case "getProductQuantity":
                            synchronized (obj) {
                                write("" + selectedProduct.getQuantity());
                            }
                            break;

                        case "getProductPrice":
                            synchronized (obj) {
                                write(""+selectedProduct.getPrice());
                            }
                            break;

                        case "getProduct":
                            synchronized (obj) {
                                write(writeProductToString(selectedProduct));
                            }
                            break;
                    }

                } catch (Exception e) {
                    System.out.println("An error occurred while trying to process a user request");
                    write("");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String writeProductToString(Product productToWrite) {
        return String.format("%s,%s,%s,%s,%d,%f", productToWrite.getSeller().getUsername(),
                productToWrite.getStore().getName(), productToWrite.getName(), productToWrite.getDescription(),
                productToWrite.getQuantity(), productToWrite.getPrice());
    }

    public static void removeProductFromCarts(Product toRemove, HashMap<String, Customer> customerMap) {
        for (Map.Entry<String, Customer> entry : Server.customerMap.entrySet()) {
            ArrayList<Product> cart = entry.getValue().getCart();
            for (int i = 0; i < cart.size(); i++) {
                Product p = cart.get(i);
                if (p.getName().equals(toRemove.getName()) &&
                        p.getStore().getName().equals(toRemove.getStore().getName())) {
                    cart.remove(i);
                    i--;
                }
            }
        }
    }
}
