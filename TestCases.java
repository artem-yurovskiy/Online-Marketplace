import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.io.*;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Test cases ensuring proper code functionality
 */

public class TestCases {
    public static void main(String[] args) {
        System.out.println("Running Test Cases for Online Marketplace");

        Result result = JUnitCore.runClasses(TestCase.class);

        if (result.wasSuccessful()) {
            System.out.println("Successfully ran");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    public static class TestCase {
        private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        private final PrintStream originalOut = System.out;

        @Before
        public void setUpStreams() {
            System.setOut(new PrintStream(outContent));
        }

        @After
        public void restoreStreams() {
            System.setOut(originalOut);
        }

        //Test Cases
        @Test(timeout = 1000)
        public void testUser() {
            User user = new User("user", "password", "seller");
            assertEquals("user", user.getUsername());
            assertEquals("password", user.getPassword());
            assertEquals("seller", user.getUserType());
            user.setUsername("username");
            user.setPassword("password1");
            user.setUserType("customer");
            assertEquals("username", user.getUsername());
            assertEquals("password1", user.getPassword());
            assertEquals("customer", user.getUserType());
        }

        @Test(timeout = 1000)
        public void testSeller() {
            Seller seller = new Seller("seller", "password", "seller");
            ArrayList<Store> storeList = new ArrayList<>();
            storeList.add(new Store("Store 1"));
            storeList.add(new Store("Store 2"));
            seller.setStoreList(storeList);
            assertEquals(0, seller.findStore("Store 1"));
            assertEquals(1, seller.findStore("Store 2"));
            assertEquals(seller.getStoreList(), storeList);
            seller.printStores();
            String expected = "Store 1\nStore 2\n";
            assertEquals(outContent.toString(), expected);
        }

        @Test(timeout = 1000)
        public void testCustomer() {
            Customer customer = new Customer("customer", "password", "customer");
            Seller seller = new Seller("seller", "password", "seller");
            Store store = new Store("Store1");
            Product product1 = new Product(seller, store, "product", "sample description", 5, 5);
            Product product2 = new Product(seller, store, "product 2", "description", 5, 5);

            // cart
            customer.addItemToCart(product1);
            Product userCart = customer.getCart().get(0);
            String expected1 = "Name: product ($5.00) | Quantity: 5 | Description: sample description";
            String expected2 = "Name: product 2 ($5.00) | Quantity: 5 | Description: description";
            assertEquals(userCart.toString(), expected1);

            // buy single item
            customer.buyItem(product1, customer.getUsername());
            Product purchasedProduct = customer.getPurchasedList().get(0);
            assertEquals(expected1, purchasedProduct.toString());

            // buy multiple
            customer.addItemToCart(product1);
            customer.addItemToCart(product2);
            customer.buyAllItems(customer.getUsername());
            purchasedProduct = customer.getPurchasedList().get(1);
            assertEquals(expected1, purchasedProduct.toString());
            purchasedProduct = customer.getPurchasedList().get(2);
            assertEquals(expected2, purchasedProduct.toString());
        }

        @Test(timeout = 1000)
        public void testProduct() {
            Seller seller = new Seller("seller", "password", "seller");
            Store store = new Store("Store1");
            Product product = new Product(seller, store, "product", "sample description", 5, 5);

            // getters
            assertEquals("ensure getName() returns the correct value.",
                    "product", product.getName());
            assertEquals("Ensure that Product method getDescription() returns the correct value.",
                    "sample description", product.getDescription());
            assertEquals("Ensure that Product method getQuantity() returns the correct value.",
                    5, product.getQuantity());
            assertEquals(5, product.getPrice(), 0.1);
            assertEquals("Ensure that Product method getSeller() returns the correct value.",
                    seller, product.getSeller());
            assertEquals("Ensure that the Product method getPrice() returns the correct value.",
                    5, product.getPrice(), .01);


            // setters
            product.setName("product 1");
            assertEquals("Ensure that the Product method setName() works correctly.",
                    "product 1", product.getName());
            product.setDescription("description");
            assertEquals("Ensure that the Product method setDescription works correctly.",
                    "description", product.getDescription());
            product.addQuantity(1);
            assertEquals("Ensure that Product method addQuantity() returns the correct value.",
                    6, product.getQuantity());
            product.removeQuantity(1);
            assertEquals("Ensure that Product method removeQuantity() returns the correct value.",
                    5, product.getQuantity());
            product.setPrice(10);
            assertEquals("Ensure that Product method setPrice() returns the correct value.",
                    10, product.getPrice(), .01);

            // equals
            Product newProduct = new Product(seller, store, "product 2", "description", 5, 5);
            assertNotEquals("Ensure that the Product method equals() works correctly", newProduct, product);
            newProduct = new Product(seller, store, "product 1", "description", 5, 10);
            assertEquals("Ensure that the Product method equals() works correctly", newProduct, product);

        }

        @Test(timeout = 1000)
        public void testStore() {
            Customer customer = new Customer("customer", "password", "customer");
            Seller seller = new Seller("seller", "password", "seller");
            Store store = new Store("Store1");
            Product product1 = new Product(seller, store, "product1", "sample description", 5, 5);
            Product product2 = new Product(seller, store, "product2", "sample description", 10, 10);

            // getters
            assertEquals("Ensure getName() works correctly.", "Store1", store.getName());
            ArrayList<Product> productList = new ArrayList<>();
            productList.add(product1);
            productList.add(product2);
            store.setProductList(productList);
            assertEquals("Ensure getProductList() works correctly.", product1, store.getProductList().get(0));
            assertEquals("Ensure getProductList() works correctly.", product2, store.getProductList().get(1));
            store.setSalesList(productList);
            assertEquals("Ensure getSalesList() works correctly.", product1, store.getSalesList().get(0));
            assertEquals("Ensure getSalesList() works correctly.", product2, store.getSalesList().get(1));
            ArrayList<String> salesNamesList = new ArrayList<>();
            salesNamesList.add("Customer 1");
            salesNamesList.add("Customer 2");
            store.setSalesNamesList(salesNamesList);
            assertEquals("Ensure getSalesList() works correctly", "Customer 1", store.getSalesNamesList().get(0));
            assertEquals("Ensure getSalesList() works correctly", "Customer 2", store.getSalesNamesList().get(1));
            // find product
            assertEquals("Ensure findProduct() works correctly.", 0, store.findProduct(product1.getName()));

            // print products
            String expected = "Name: product1 ($5.00) | Quantity: 5 | Description: sample description\n" +
                    "Name: product2 ($10.00) | Quantity: 10 | Description: sample description\n";
            store.printProducts();
            assertEquals(outContent.toString(), expected);
            // setters
            store.setName("Store 1");
            assertEquals("Ensure setName() works correctly.", "Store 1", store.getName());

            // edit product
            store.editProduct(product1.getName(), "product", "name");
            assertEquals("Ensure edit method works correctly.",
                    "product", store.getProductList().get(store.findProduct("product")).getName());
            store.editProduct(product1.getName(), "new description", "description");
            assertEquals("Ensure edit method works correctly.",
                    "new description", store.getProductList().get(store.findProduct("product")).getDescription());
            store.editProduct(product1.getName(), "10", "quantity");
            assertEquals("Ensure edit method works correctly.",
                    10, store.getProductList().get(store.findProduct("product")).getQuantity());
            store.editProduct(product1.getName(), "100", "price");
            assertEquals("Ensure edit method works correctly.",
                    100, store.getProductList().get(store.findProduct("product")).getPrice(), .01);
            // remove product
            store.removeProduct(product1);
            assertEquals("Ensure remove method works correctly.", product2, store.getProductList().get(0));
        }
    }
}