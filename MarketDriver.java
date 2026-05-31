import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * This class runs all the GUI code on the client side. It works by initializing one JFrame
 * which is then updated to contain a new JPanel every time the user clicks a button to
 * move to a new screen
 */

public class MarketDriver implements Runnable {
    private static JFrame frame = new JFrame();
    private static MarketPlace marketPlace;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public void run() {
        tryConnectServer();
    }

    private boolean tryConnectServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            marketPlace = new MarketPlace(oos, ois);
            System.out.println("Successfully connected with server");
            setWelcome();
        } catch (Exception e) {
            System.out.println("Error connecting with server");
            setNoServer();
            return false;
        }
        return true;
    }

    private void setNoServer() {
        frame.getContentPane().removeAll();
        frame.setName("No Server Connection");

        JButton exitButton = new JButton("Quit");
        JButton connectServer = new JButton("Try Connection Again");

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));

        JLabel label = new JLabel("Error connecting with server");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 25));

        panel.add(label);
        panel.add(connectServer);
        panel.add(exitButton);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> {
            frame.dispose();
        });
        connectServer.addActionListener(e -> {
            if (tryConnectServer()) {
                setWelcome();
            } else {
                JOptionPane.showMessageDialog(null, "Connection failed",
                        "Server Down", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MarketDriver());
    }

    private static void initializeFrame(int frameWidth, int frameHeight) {
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.repaint();
        frame.revalidate();
        frame.setVisible(true);
    }

    private static void setWelcome() {
        frame.getContentPane().removeAll();
        frame.setName("Homepage");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Welcome to the Marketplace");
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login");
        JButton quitButton = new JButton("Exit");

        panel.add(label);
        panel.add(createAccountButton);
        panel.add(loginButton);
        panel.add(quitButton);

        frame.add(panel);
        initializeFrame(600, 400);

        createAccountButton.addActionListener(e -> setCreateAccount());
        loginButton.addActionListener(e -> setLogin());

        quitButton.addActionListener(e -> {
            frame.dispose();
        });
    }

    public static void setLogin() {
        frame.getContentPane().removeAll();
        frame.setName("Login");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(110, 10, 110, 10));

        JPanel username = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 30));
        username.add(usernameLabel);
        username.add(usernameField);

        JPanel password = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 30));
        password.add(passwordLabel);
        password.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");
        buttons.add(loginButton);
        buttons.add(exitButton);

        panel.add(username);
        panel.add(password);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setWelcome());
        loginButton.addActionListener(e -> {
            String userType = marketPlace.login(usernameField.getText(),
                    String.valueOf(passwordField.getPassword()), usernameField, passwordField);
            if (!userType.isEmpty()) {
                if (userType.equals("seller")) {
                    setSellerDashboard();
                } else {
                    setCustomerDashboard();
                }
            }
        });
    }

    public static void setCreateAccount() {
        frame.getContentPane().removeAll();
        frame.setName("Create Account");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(110, 10, 110, 10));

        JPanel radioButtonPanel = new JPanel();
        JRadioButton sellerOption = new JRadioButton("Seller");
        JRadioButton customerOption = new JRadioButton("Customer");
        ButtonGroup group = new ButtonGroup();
        group.add(sellerOption);
        group.add(customerOption);
        radioButtonPanel.add(sellerOption);
        radioButtonPanel.add(customerOption);

        JPanel username = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 30));
        username.add(usernameLabel);
        username.add(usernameField);

        JPanel password = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 30));
        password.add(passwordLabel);
        password.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton createAccountButton = new JButton("Create Account");
        JButton exitButton = new JButton("Exit");
        buttons.add(createAccountButton);
        buttons.add(exitButton);

        panel.add(radioButtonPanel);
        panel.add(username);
        panel.add(password);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setWelcome());
        createAccountButton.addActionListener(e -> {
            boolean created = marketPlace.createAccount(usernameField.getText(),
                    String.valueOf(passwordField.getPassword()),
                    sellerOption.isSelected(), customerOption.isSelected());
            if (created) {
                if (sellerOption.isSelected()) {
                    setSellerDashboard();
                } else {
                    setCustomerDashboard();
                }
            }
        });
    }

    public static void setCustomerDashboard() {
        frame.getContentPane().removeAll();
        frame.setName("Customer Dashboard");

        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        String username = marketPlace.getUsername();
        JLabel label = new JLabel("Customer | " + username + "'s Dashboard");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton settings = new JButton("Edit Account");
        JButton viewAll = new JButton("View All Items in Marketplace");
        JButton search = new JButton("Search for Item");
        JButton cart = new JButton("View Cart");
        JButton history = new JButton("View Purchase History");
        JButton statistics = new JButton("View Statistics");
        JButton logout = new JButton("Logout");

        panel.add(label);
        panel.add(settings);
        panel.add(viewAll);
        panel.add(search);
        panel.add(cart);
        panel.add(history);
        panel.add(statistics);
        panel.add(logout);

        frame.add(panel);
        initializeFrame(1000, 700);

        settings.addActionListener(e -> setEditAccount());
        viewAll.addActionListener(e -> {
            String allItems = marketPlace.getAllItems();
            if (allItems.isEmpty()) {
                JOptionPane.showMessageDialog(null, "There are currently no items for sale in the marketplace",
                        "No Items for Sale", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewItems(allItems, 0, "", "");
            }
        });
        search.addActionListener(e -> setSearchForItem());
        cart.addActionListener(e -> setViewCart());
        history.addActionListener(e -> {
            if (marketPlace.getCustomerShoppingHistory().isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have not purchased any items from any stores",
                        "No Purchase History", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewPurchaseHistory();
            }
        });
        statistics.addActionListener(e -> setViewCustomerStatistics());
        logout.addActionListener(e -> setWelcome());
    }

    public static void setSellerDashboard() {
        frame.getContentPane().removeAll();
        frame.setName("Seller Dashboard");

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        String username = marketPlace.getUsername();
        JLabel label = new JLabel("Seller | " + username + "'s Dashboard");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton settings = new JButton("Edit Account");
        JButton viewStores = new JButton("View Stores");
        JButton createStore = new JButton("Create Store");
        JButton logout = new JButton("Logout");

        panel.add(label);
        panel.add(settings);
        panel.add(viewStores);
        panel.add(createStore);
        panel.add(logout);

        frame.add(panel);
        initializeFrame(1000, 700);

        settings.addActionListener(e -> setEditAccount());
        viewStores.addActionListener(e -> {
            String stores = marketPlace.getStores();
            if (stores.isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have no stores to view",
                        "No Stores", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewStores();
            }
        });
        createStore.addActionListener(e -> setCreateStore());
        logout.addActionListener(e -> setWelcome());
    }

    public static void setEditAccount() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Account");

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Edit Account");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton changeUsername = new JButton("Change username");
        JButton changePassword = new JButton("Change password");
        JButton deleteAccount = new JButton("Delete Account");
        JButton exit = new JButton("Exit");

        panel.add(label);
        panel.add(changeUsername);
        panel.add(changePassword);
        panel.add(deleteAccount);
        panel.add(exit);

        frame.add(panel);
        initializeFrame(1000, 700);
        changeUsername.addActionListener(e -> setChangeUsername());
        changePassword.addActionListener(e -> setChangePassword());
        deleteAccount.addActionListener(e -> setDeleteAccount());
        exit.addActionListener(e -> {
            String userType = marketPlace.getUserType();
            if (userType.equals("seller")) {
                setSellerDashboard();
            } else {
                setCustomerDashboard();
            }
        });
    }

    public static void setViewItems(String products, int sort, String searchBy, String searchFor) {
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are currently no items for sale in the marketplace",
                    "No Items for Sale", JOptionPane.ERROR_MESSAGE);
            setCustomerDashboard();
            return;
        }

        frame.getContentPane().removeAll();
        frame.setName("Stores");

        String[] items = products.split("~");
        ArrayList<Trip<String, Double, Integer>> tripList = new ArrayList<>();
        for (String s : items) {
            String[] a = s.split(",");
            tripList.add(new Trip<>(s, Double.parseDouble(a[5]), Integer.parseInt(a[4])));
        }

        if (sort == 1 || sort == 2) {
            tripList.sort(Comparator.comparing(pair -> pair.second));
            if (sort == 2) {
                Collections.reverse(tripList);
            }
        }
        if (sort == 3 || sort == 4) {
            tripList.sort(Comparator.comparing(pair -> pair.third));
            if (sort == 4) {
                Collections.reverse(tripList);
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Select a Product to View");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (Trip<String, Double, Integer> trip : tripList) {
            String[] prod = trip.first.split(",");
            String str = String.format("%s | %s | Available: %d | $%.2f", prod[1], prod[2], Integer.parseInt(prod[4])
                    , Double.parseDouble(prod[5]));
            JButton productButton = new JButton(str);
            productButton.setPreferredSize(new Dimension(800, 50));
            productButton.setMinimumSize(new Dimension(800, 50));
            productButton.setMaximumSize(new Dimension(800, 50));
            productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            productButton.addActionListener(e -> {
                marketPlace.setProduct(prod[2], prod[1], prod[0]);
                setViewProduct(sort, searchBy, searchFor);
            });

            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(productButton);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomButtons = new JPanel(new FlowLayout());
        String[] sortChoices = {"Default", "Price: low to high", "Price: high to low",
            "Quantity: low to high", "Quantity: high to low"};

        int placeholder;
        if (sort != 0) {
            sortChoices = Arrays.copyOfRange(sortChoices, 1, sortChoices.length);
            placeholder = 1;
        } else {
            placeholder = 0;
        }

        JComboBox<String> sortComboBox = new JComboBox<>(sortChoices);
        sortComboBox.setSelectedIndex(sort - placeholder);
        bottomButtons.add(sortComboBox);

        JButton submitSort = new JButton("Submit");
        bottomButtons.add(submitSort);

        JLabel space = new JLabel("     ");
        bottomButtons.add(space);

        JButton exitButton = new JButton("Exit");
        bottomButtons.add(exitButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomButtons, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        submitSort.addActionListener(e -> {
            String itemsByCategory = marketPlace.getItemsByCategory(searchBy, searchFor);
            setViewItems(itemsByCategory, sortComboBox.getSelectedIndex() + placeholder, searchBy, searchFor);

        });
        exitButton.addActionListener(e -> setCustomerDashboard());
    }

    public static void setSearchForItem() {
        frame.getContentPane().removeAll();
        frame.setName("Search for Item");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(140, 80, 160, 80));
        JLabel label = new JLabel("Enter Search Criteria");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 25));

        JPanel searchPanel1 = new JPanel(new FlowLayout());
        String[] searchCriteria = new String[]{"Product Name", "Product Description", "Store"};
        JComboBox searchCriteriaDropdown = new JComboBox<>(searchCriteria);
        searchCriteriaDropdown.setPreferredSize(new Dimension(175, 30));
        searchPanel1.add(searchCriteriaDropdown);

        JPanel searchPanel2 = new JPanel(new FlowLayout());
        JLabel termLabel = new JLabel("Search Term: ");
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(350, 35));
        searchPanel2.add(termLabel);
        searchPanel2.add(searchField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton searchButton = new JButton("Search");
        JButton exitButton = new JButton("Exit");
        buttons.add(searchButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(searchPanel1);
        panel.add(searchPanel2);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(700, 500);

        exitButton.addActionListener(e -> setCustomerDashboard());
        searchButton.addActionListener(e -> {
            if (searchField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Search term cannot be empty",
                        "Search Term Empty", JOptionPane.ERROR_MESSAGE);
            } else {
                String searchedProducts = marketPlace.getItemsByCategory(searchCriteriaDropdown
                        .getSelectedItem().toString(), searchField.getText());
                if (searchedProducts.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No products match the search criteria",
                            "No Products Found", JOptionPane.ERROR_MESSAGE);
                } else {
                    setViewItems(searchedProducts, 0,
                            searchCriteriaDropdown.getSelectedItem().toString(), searchField.getText());
                }
            }
        });
    }

    public static void setViewCart() {
        frame.getContentPane().removeAll();
        frame.setName("Cart");

        String productsInCart = marketPlace.getCart();
        JButton exitButton = new JButton("Exit");

        if (productsInCart.isEmpty()) {
            JPanel panel = new JPanel(new GridLayout(2, 1));
            panel.setBorder(BorderFactory.createEmptyBorder(240, 220, 280, 220));

            JLabel label = new JLabel("Cart is Empty");
            label.setHorizontalAlignment(0);
            label.setFont(new Font("SANS_SERIF", Font.BOLD, 35));

            panel.add(label);
            panel.add(exitButton);

            frame.add(panel);
        } else {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

            JLabel label = new JLabel("Select Product to Remove from Cart");
            label.setHorizontalAlignment(0);
            label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
            label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

            String[] products = productsInCart.split("~");
            double totalPrice = 0;
            for (String product : products) {
                String[] productInfo = product.split(",");
                int productQuantity = Integer.parseInt(productInfo[4]);
                Double productPrice = Double.parseDouble(productInfo[5]);

                double price = productPrice * productQuantity;
                totalPrice += price;
                JButton productButton = new JButton(productQuantity + " x "
                        + productInfo[2] + ": $" + String.format("%.2f", price));
                productButton.setPreferredSize(new Dimension(800, 50));
                productButton.setMinimumSize(new Dimension(800, 50));
                productButton.setMaximumSize(new Dimension(800, 50));
                productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                productButton.addActionListener(e -> {
                    marketPlace.removeItemFromCart(productInfo[2], productQuantity);
                    setViewCart();
                });

                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                panel.add(productButton);
            }

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel bottomPanel = new JPanel(new FlowLayout());
            JLabel totalPriceLabel = new JLabel("Total: $" + String.format("%.2f", totalPrice));
            JButton purchaseCartButton = new JButton("Purchase Cart");
            bottomPanel.add(totalPriceLabel);
            bottomPanel.add(purchaseCartButton);
            bottomPanel.add(exitButton);

            frame.add(label, BorderLayout.NORTH);
            frame.add(scrollPane);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            purchaseCartButton.addActionListener(e -> {
                marketPlace.purchaseCart();
                setCustomerDashboard();
            });

        }

        initializeFrame(1000, 700);
        exitButton.addActionListener(e -> setCustomerDashboard());
    }

    public static void setViewPurchaseHistory() {
        frame.getContentPane().removeAll();
        frame.setName("Purchase History");

        String[] purchases = marketPlace.getCustomerShoppingHistory().split("~");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Purchase History");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String purchase : purchases) {
            String[] purchaseInfo = purchase.split(",");

            JLabel previousPurchase = new JLabel("Store: " + purchaseInfo[1]
                    + " | Product Name: " + purchaseInfo[2] + " | Quantity: " + purchaseInfo[4]);
            previousPurchase.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(previousPurchase);
            panel.add(Box.createVerticalStrut(10));
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Export to File");
        exportButton.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(exportButton);
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(exitButton);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        initializeFrame(1000, 700);
        exitButton.addActionListener(e -> setCustomerDashboard());
        exportButton.addActionListener(e -> marketPlace.exportPurchaseHistory());
    }

    public static void setViewCustomerStatistics() {
        frame.getContentPane().removeAll();
        frame.setName("Customer statistics");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Customer Statistics");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton byStore = new JButton("View by stores");
        JButton byProduct = new JButton("View by products purchased");
        JButton quitButton = new JButton("Quit");

        panel.add(label);
        panel.add(byStore);
        panel.add(byProduct);
        panel.add(quitButton);

        frame.add(panel);
        initializeFrame(600, 400);

        byStore.addActionListener(e -> {
            if (marketPlace.getPurchasedList().isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have not made any purchases",
                        "No Purchases Made", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewCustomerStatisticsByStore(0);
            }

        });
        byProduct.addActionListener(e -> {
            if (marketPlace.getPurchasedList().isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have not made any purchases",
                        "No Purchases Made", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewCustomerStatisticsByProduct(0);
            }
        });
        quitButton.addActionListener(e -> setCustomerDashboard());
    }

    public static void setViewCustomerStatisticsByStore(int sort) {
        frame.getContentPane().removeAll();
        frame.setName("Customer Statistics by Store");

        String[] customersStats = marketPlace.getCustomerStatisticsByStore().split("~");

        ArrayList<Pair<String, Integer>> pairList = new ArrayList<>();
        for (String stat : customersStats) {
            String[] statsDetail = stat.split("/");
            pairList.add(new Pair<>(statsDetail[0], Integer.parseInt(statsDetail[1])));
        }

        if (sort == 1 || sort == 2) {
            pairList.sort(Comparator.comparing(pair -> pair.second));
            if (sort == 2) {
                Collections.reverse(pairList);
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Customer Statistics by Store");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (Pair<String, Integer> p : pairList) {
            JLabel statsLabel = new JLabel(p.second + " products bought from " + p.first);
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(statsLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomButtons = new JPanel(new FlowLayout());

        String[] sortChoices = {"Default", "Low to high", "High to low"};
        int placeholder;
        if (sort != 0) {
            sortChoices = Arrays.copyOfRange(sortChoices, 1, sortChoices.length);
            placeholder = 1;
        } else {
            placeholder = 0;
        }

        JComboBox<String> sortComboBox = new JComboBox<>(sortChoices);
        sortComboBox.setSelectedIndex(sort - placeholder);
        bottomButtons.add(sortComboBox);

        JButton submitSort = new JButton("Submit");
        bottomButtons.add(submitSort);

        JLabel space = new JLabel("     ");
        bottomButtons.add(space);

        JButton exitButton = new JButton("Exit");
        bottomButtons.add(exitButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomButtons, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        submitSort.addActionListener(e ->
                setViewCustomerStatisticsByStore(sortComboBox.getSelectedIndex() + placeholder));
        exitButton.addActionListener(e -> setViewCustomerStatistics());
    }

    public static void setViewCustomerStatisticsByProduct(int sort) {
        frame.getContentPane().removeAll();
        frame.setName("Customer Statistics by Product");

        String[] customersStats = marketPlace.getCustomerStatisticsByProduct().split("~");

        ArrayList<Pair<String, Integer>> pairList = new ArrayList<>();
        for (String s : customersStats) {
            String[] yuh = s.split("/");
            String prod = yuh[0].split(",")[2];
            pairList.add(new Pair<>(prod, Integer.parseInt(yuh[1])));
        }

        if (sort == 1 || sort == 2) {
            pairList.sort(Comparator.comparing(pair -> pair.second));
            if (sort == 2) {
                Collections.reverse(pairList);
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Customer Statistics by Product");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (Pair<String, Integer> p : pairList) {
            JLabel statsLabel = new JLabel(p.first + " bought " + p.second + " times");
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(statsLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomButtons = new JPanel(new FlowLayout());

        String[] sortChoices = {"Default", "Low to high", "High to low"};
        int placeholder;
        if (sort != 0) {
            sortChoices = Arrays.copyOfRange(sortChoices, 1, sortChoices.length);
            placeholder = 1;
        } else {
            placeholder = 0;
        }

        JComboBox<String> sortComboBox = new JComboBox<>(sortChoices);
        sortComboBox.setSelectedIndex(sort - placeholder);
        bottomButtons.add(sortComboBox);

        JButton submitSort = new JButton("Submit");
        bottomButtons.add(submitSort);

        JLabel space = new JLabel("     ");
        bottomButtons.add(space);

        JButton exitButton = new JButton("Exit");
        bottomButtons.add(exitButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomButtons, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        submitSort.addActionListener(e ->
                setViewCustomerStatisticsByProduct(sortComboBox.getSelectedIndex() + placeholder));
        exitButton.addActionListener(e -> setViewCustomerStatistics());
    }

    public static void setViewStores() {
        frame.getContentPane().removeAll();
        frame.setName("Stores");

        String[] stores = marketPlace.getStores().split(",");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Select a Store to View");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String userStoreName : stores) {
            JButton storeButton = new JButton(userStoreName);
            storeButton.setPreferredSize(new Dimension(300, 70));
            storeButton.setMinimumSize(new Dimension(300, 70));
            storeButton.setMaximumSize(new Dimension(300, 70));
            storeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            storeButton.addActionListener(e -> {
                marketPlace.setStore(userStoreName);
                setStoreDashboard();
            });

            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(storeButton);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(300, 65));

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(exitButton, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        exitButton.addActionListener(e -> setSellerDashboard());
    }

    public static void setCreateStore() {
        frame.getContentPane().removeAll();
        frame.setName("Create Store");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 80, 100, 80));

        JLabel label = new JLabel("Create Store");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel store = new JPanel(new FlowLayout());
        JLabel storeLabel = new JLabel("Store Name: ");
        JTextField storeField = new JTextField();
        storeField.setPreferredSize(new Dimension(150, 30));
        store.add(storeLabel);
        store.add(storeField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton createStoreButton = new JButton("Create Store");
        JButton exitButton = new JButton("Exit");
        buttons.add(createStoreButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(store);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        createStoreButton.addActionListener(e -> {
            boolean storeCreated = marketPlace.addStore(storeField.getText());
            if (storeCreated) {
                JOptionPane.showMessageDialog(null, "Store successfully created",
                        "Store Created", JOptionPane.INFORMATION_MESSAGE);
                setSellerDashboard();
            }
        });
        exitButton.addActionListener(e -> setSellerDashboard());
    }

    public static void setViewProductsInCarts() {
        frame.getContentPane().removeAll();
        frame.setName("Products in Customer Carts");

        String[] productsInCarts = marketPlace.getCustomerCarts().split("~");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel(productsInCarts.length + " Products in Customer Carts");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String customerCart : productsInCarts) {
            String[] customerCartInfo = customerCart.split(",");
            JLabel statsLabel = new JLabel(customerCartInfo[1] + " x " + customerCartInfo[0] + " in customer carts");
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(statsLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(300, 65));

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(exitButton, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        exitButton.addActionListener(e -> setStoreDashboard());
    }

    public static void setChangeUsername() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Account");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 80, 100, 80));
        JLabel label = new JLabel("Current Username: " + marketPlace.getUsername());
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel username = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("New Username:       ");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 30));
        username.add(usernameLabel);
        username.add(usernameField);

        JPanel confirmUsername = new JPanel(new FlowLayout());
        JLabel confirmUsernameLabel = new JLabel("Confirm Username: ");
        JTextField confirmUsernameField = new JTextField();
        confirmUsernameField.setPreferredSize(new Dimension(150, 30));
        confirmUsername.add(confirmUsernameLabel);
        confirmUsername.add(confirmUsernameField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changeUsername = new JButton("Change Username");
        JButton exitButton = new JButton("Exit");
        buttons.add(changeUsername);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(username);
        panel.add(confirmUsername);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setEditAccount());
        changeUsername.addActionListener(e -> {
            boolean changed = marketPlace.changeUsername(usernameField.getText(), confirmUsernameField.getText(),
                    usernameField, confirmUsernameField);
            if (changed) {
                if (marketPlace.getUserType().equals("seller")) {
                    setSellerDashboard();
                } else {
                    setCustomerDashboard();
                }
            }
        });
    }

    public static void setChangePassword() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Account");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 80, 100, 80));

        JLabel label = new JLabel("Change Password");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel password = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("New Password:       ");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 30));
        password.add(passwordLabel);
        password.add(passwordField);

        JPanel confirmPassword = new JPanel(new FlowLayout());
        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(150, 30));
        confirmPassword.add(confirmPasswordLabel);
        confirmPassword.add(confirmPasswordField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changePassword = new JButton("Change Password");
        JButton exitButton = new JButton("Exit");
        buttons.add(changePassword);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(password);
        panel.add(confirmPassword);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setEditAccount());
        changePassword.addActionListener(e -> {
            boolean changed = marketPlace.changePassword(String.valueOf(passwordField.getPassword()),
                    String.valueOf(confirmPasswordField.getPassword()));
            if (changed) {
                if (marketPlace.getUserType().equals("seller")) {
                    setSellerDashboard();
                } else {
                    setCustomerDashboard();
                }
            }
        });
    }

    public static void setDeleteAccount() {
        frame.getContentPane().removeAll();
        frame.setName("Delete Account");

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 0, 130, 0));

        JLabel label = new JLabel("Are you sure you want to delete your account?");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton deleteAccountButton = new JButton("Delete account");
        deleteAccountButton.setForeground(Color.RED);
        JButton exitButton = new JButton("Exit");
        buttons.add(deleteAccountButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setEditAccount());
        deleteAccountButton.addActionListener(e -> {
            marketPlace.deleteAccount();
            setWelcome();
            JOptionPane.showMessageDialog(null, "Successfully deleted account",
                    "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public static void setStoreDashboard() {
        frame.getContentPane().removeAll();
        frame.setName("Store");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Store | " + marketPlace.getStoreName() + " Dashboard");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton edit = new JButton("Edit Store");
        JButton addProduct = new JButton("Add Product to Store");
        JButton viewProducts = new JButton("View Products in Store");
        JButton importProducts = new JButton("Import Products from File");
        JButton exportProducts = new JButton("Export Products to File");
        JButton viewCart = new JButton("View Products in Customer Carts");
        JButton statistics = new JButton("View Statistics");
        JButton logout = new JButton("Exit Store");

        panel.add(label);
        panel.add(edit);
        panel.add(addProduct);
        panel.add(viewProducts);
        panel.add(importProducts);
        panel.add(exportProducts);
        panel.add(viewCart);
        panel.add(statistics);
        panel.add(logout);

        frame.add(panel);
        initializeFrame(1000, 700);

        edit.addActionListener(e -> editStore());
        addProduct.addActionListener(e -> setAddProductToStore());
        viewProducts.addActionListener(e -> {
            if (marketPlace.getProductList().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Store has no products to view",
                        "No Products", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewProductsInStore();
            }
        });
        importProducts.addActionListener(e -> setImportProductsFromFile());
        exportProducts.addActionListener(e -> marketPlace.exportProducts());
        viewCart.addActionListener(e -> {
            String stringCustomerCarts = marketPlace.getCustomerCarts();
            if (stringCustomerCarts.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No customer has any of your products in cart",
                        "No Items in Cart", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewProductsInCarts();
            }
        });
        statistics.addActionListener(e -> setViewStoreStatistics());
        logout.addActionListener(e -> setSellerDashboard());
    }

    public static void editStore() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Store");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Edit Store");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton changeName = new JButton("Change name");
        JButton deleteStore = new JButton("Delete Store");
        JButton exit = new JButton("Exit");

        panel.add(label);
        panel.add(changeName);
        panel.add(deleteStore);
        panel.add(exit);

        frame.add(panel);
        initializeFrame(1000, 700);

        changeName.addActionListener(e -> setChangeStoreName());
        deleteStore.addActionListener(e -> setDeleteStore());
        exit.addActionListener(e -> {
            if (marketPlace.getUserType().equals("seller")) {
                setStoreDashboard();
            } else {
                setCustomerDashboard();
            }
        });
    }

    public static void setChangeStoreName() {
        frame.getContentPane().removeAll();
        frame.setName("Change Store Name");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 80, 100, 80));

        JLabel label = new JLabel("Current Store Name: " + marketPlace.getStoreName());
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel store = new JPanel(new FlowLayout());
        JLabel storeLabel = new JLabel("New Store Name:      ");
        JTextField storeField = new JTextField();
        storeField.setPreferredSize(new Dimension(150, 30));
        store.add(storeLabel);
        store.add(storeField);

        JPanel confirmStore = new JPanel(new FlowLayout());
        JLabel confirmStoreLabel = new JLabel("Confirm Store Name: ");
        JTextField confirmStoreField = new JTextField();
        confirmStoreField.setPreferredSize(new Dimension(150, 30));
        confirmStore.add(confirmStoreLabel);
        confirmStore.add(confirmStoreField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changeStoreName = new JButton("Change Store Name");
        JButton exitButton = new JButton("Exit");
        buttons.add(changeStoreName);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(store);
        panel.add(confirmStore);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setStoreDashboard());
        changeStoreName.addActionListener(e -> {
            boolean storeNameChanged = marketPlace.changeStoreName(storeField.getText(), confirmStoreField.getText());
            if (storeNameChanged) {
                setStoreDashboard();
            }
        });
    }

    public static void setAddProductToStore() {
        frame.getContentPane().removeAll();
        frame.setName("Add Product");

        JPanel panel = new JPanel(new GridLayout(6, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 80, 110, 80));
        JLabel label = new JLabel("Enter Product Information");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 25));

        JPanel productName = new JPanel();
        JLabel productNameLabel = new JLabel("Product Name: ");
        JTextField productNameField = new JTextField();
        productNameField.setPreferredSize(new Dimension(150, 30));
        productName.add(productNameLabel);
        productName.add(productNameField);

        JPanel productDescription = new JPanel();
        JLabel productDescriptionLabel = new JLabel("Product Description: ");
        JTextField productDescriptipnField = new JTextField();
        productDescriptipnField.setPreferredSize(new Dimension(300, 30));
        productDescription.add(productDescriptionLabel);
        productDescription.add(productDescriptipnField);

        JPanel productQuantity = new JPanel();
        JLabel productQuantityLabel = new JLabel("Product Quantity: ");
        JTextField productQuantityField = new JTextField();
        productQuantityField.setPreferredSize(new Dimension(80, 30));
        productQuantity.add(productQuantityLabel);
        productQuantity.add(productQuantityField);

        JPanel productPrice = new JPanel();
        JLabel productPriceLabel = new JLabel("Product Price: ");
        JTextField productPriceField = new JTextField();
        productPriceField.setPreferredSize(new Dimension(80, 30));
        productPrice.add(productPriceLabel);
        productPrice.add(productPriceField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addProduct = new JButton("Add Product to Store");
        JButton exitButton = new JButton("Exit");
        buttons.add(addProduct);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(productName);
        panel.add(productDescription);
        panel.add(productQuantity);
        panel.add(productPrice);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(700, 500);

        exitButton.addActionListener(e -> setStoreDashboard());
        addProduct.addActionListener(e -> {
            boolean addedProduct = marketPlace.addProduct(productNameField.getText(),
                    productDescriptipnField.getText(), productQuantityField.getText(), productPriceField.getText());
            if (addedProduct) {
                setStoreDashboard();
            }
        });
    }

    public static void setViewProductsInStore() {
        frame.getContentPane().removeAll();
        frame.setName("View Products");

        String[] products = marketPlace.getProductList().split("~");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Select a Item to Edit");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String p : products) {
            String[] productInfo = p.split(",");
            String prod = String.format("%s | $%.2f | x%d | %s", productInfo[2],
                    Double.parseDouble(productInfo[5]), Integer.parseInt(productInfo[4]), productInfo[3]);
            JButton productButton = new JButton(prod);
            productButton.setPreferredSize(new Dimension(800, 50));
            productButton.setMinimumSize(new Dimension(800, 50));
            productButton.setMaximumSize(new Dimension(800, 50));
            productButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            productButton.addActionListener(e -> {
                marketPlace.setProduct(productInfo[2], productInfo[1], productInfo[0]);
                setProductDashboard();
            });

            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(productButton);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(300, 65));

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(exitButton, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        exitButton.addActionListener(e -> setStoreDashboard());
    }

    public static void setImportProductsFromFile() {
        frame.getContentPane().removeAll();
        frame.setName("Import Products");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 80, 100, 80));

        JLabel label = new JLabel("Enter CSV file with products");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel filename = new JPanel(new FlowLayout());
        JLabel filenameLabel = new JLabel("Filename: ");
        JTextField filenameField = new JTextField();
        filenameField.setPreferredSize(new Dimension(150, 30));
        filename.add(filenameLabel);
        filename.add(filenameField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton importProductsButton = new JButton("Import Products");
        JButton exitButton = new JButton("Exit");
        buttons.add(importProductsButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(filename);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        importProductsButton.addActionListener(e -> {
            boolean productsImported = marketPlace.importProducts(filenameField.getText());
            if (productsImported) {
                setStoreDashboard();
            }
        });
        exitButton.addActionListener(e -> setStoreDashboard());
    }

    public static void setViewStoreStatistics() {
        frame.getContentPane().removeAll();
        frame.setName("Statistics");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Store Statistics");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton customersStatisticsButton = new JButton("View customers statistics");
        JButton productStatisticsButton = new JButton("View product statistics");
        JButton quitButton = new JButton("Quit");

        panel.add(label);
        panel.add(customersStatisticsButton);
        panel.add(productStatisticsButton);
        panel.add(quitButton);

        frame.add(panel);
        initializeFrame(600, 400);

        customersStatisticsButton.addActionListener(e -> {
            if (marketPlace.getSalesNamesList().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No customer has purchased an item from this store",
                        "Empty Customer List", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewStoreStatisticsByCustomer(0);
            }

        });
        productStatisticsButton.addActionListener(e -> {
            if (marketPlace.getSalesList().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No products have been bought from this store",
                        "Empty Customer List", JOptionPane.ERROR_MESSAGE);
            } else {
                setViewStoreStatisticsByProduct(0);
            }
        });
        quitButton.addActionListener(e -> setStoreDashboard());
    }

    public static void setDeleteStore() {
        frame.getContentPane().removeAll();
        frame.setName("Delete Store");

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 0, 130, 0));

        JLabel label = new JLabel("Are you sure you want to delete this store?");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton deleteStoreButton = new JButton("Delete Store");
        deleteStoreButton.setForeground(Color.RED);
        JButton exitButton = new JButton("Exit");
        buttons.add(deleteStoreButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setStoreDashboard());
        deleteStoreButton.addActionListener(e -> {
            marketPlace.deleteStore();
            setSellerDashboard();
        });

    }

    public static void setProductDashboard() {
        frame.getContentPane().removeAll();
        frame.setName("Product");

        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 50, 80));

        JLabel label = new JLabel("Product | " + marketPlace.getProductName() + " Dashboard");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        JButton editNameButton = new JButton("Edit Name");
        JButton editDescriptionButton = new JButton("Edit Description");
        JButton editQuantityButton = new JButton("Edit Quantity");
        JButton editPriceButton = new JButton("Edit Price");
        JButton deleteProductButton = new JButton("Delete Product");
        JButton quitButton = new JButton("Quit");

        panel.add(label);
        panel.add(editNameButton);
        panel.add(editDescriptionButton);
        panel.add(editQuantityButton);
        panel.add(editPriceButton);
        panel.add(deleteProductButton);
        panel.add(quitButton);

        frame.add(panel);
        initializeFrame(1000, 700);

        editNameButton.addActionListener(e -> setEditName());
        editDescriptionButton.addActionListener(e -> setEditDescription());
        editQuantityButton.addActionListener(e -> setEditQuantity());
        editPriceButton.addActionListener(e -> setEditPrice());
        deleteProductButton.addActionListener(e -> setDeleteProduct());
        quitButton.addActionListener(e -> setStoreDashboard());
    }

    public static void setEditName() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Product Name");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(85, 80, 85, 80));

        JLabel label = new JLabel("Current Name: " + marketPlace.getProductName());
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.PLAIN, 15));

        JPanel product = new JPanel(new FlowLayout());
        JLabel productLabel = new JLabel("New Product Name: ");
        JTextField productField = new JTextField();
        productField.setPreferredSize(new Dimension(150, 30));
        product.add(productLabel);
        product.add(productField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changeNameButton = new JButton("Change Product Name");
        JButton exitButton = new JButton("Exit");
        buttons.add(changeNameButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(product);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setProductDashboard());
        changeNameButton.addActionListener(e -> {
            if (marketPlace.changeProductName(productField.getText())) {
                setProductDashboard();
            }
        });
    }

    public static void setEditDescription() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Product Description");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(85, 80, 85, 80));

        JLabel label = new JLabel("Current Description: " + marketPlace.getProductDescription());
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.PLAIN, 15));
        label.setPreferredSize(new Dimension(400, 200));

        JPanel product = new JPanel(new FlowLayout());
        JLabel productLabel = new JLabel("New Product Description: ");
        JTextField productField = new JTextField();
        productField.setPreferredSize(new Dimension(225, 30));
        product.add(productLabel);
        product.add(productField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changeDescriptionButton = new JButton("Change Description");
        JButton exitButton = new JButton("Exit");
        buttons.add(changeDescriptionButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(product);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setProductDashboard());
        changeDescriptionButton.addActionListener(e -> {
            if (marketPlace.changeProductDescription(productField.getText())) {
                setProductDashboard();
            }
        });
    }

    public static void setEditQuantity() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Quantity");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(85, 80, 85, 80));

        JLabel label = new JLabel("Current Quantity: " + marketPlace.getProductQuanity());
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.PLAIN, 15));

        JPanel product = new JPanel(new FlowLayout());
        JLabel productLabel = new JLabel("New Product Quantity: ");
        JTextField productField = new JTextField();
        productField.setPreferredSize(new Dimension(150, 30));
        product.add(productLabel);
        product.add(productField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton changeQuantityButton = new JButton("Change Quantity");
        JButton exitButton = new JButton("Exit");
        buttons.add(changeQuantityButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(product);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setProductDashboard());
        changeQuantityButton.addActionListener(e -> {
            if (marketPlace.changeProductQuantity(productField.getText())) {
                setProductDashboard();
            }
        });
    }

    public static void setEditPrice() {
        frame.getContentPane().removeAll();
        frame.setName("Edit Price");

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(85, 80, 85, 80));

        JLabel label = new JLabel("Current Price: " + String.format("%.2f",
                Double.parseDouble(marketPlace.getProductPrice())));
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.PLAIN, 15));

        JPanel product = new JPanel(new FlowLayout());
        JLabel productLabel = new JLabel("New Product Price: ");
        JTextField productField = new JTextField();
        productField.setPreferredSize(new Dimension(150, 30));
        product.add(productLabel);
        product.add(productField);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton editPriceButton = new JButton("Change Price");
        JButton exitButton = new JButton("Exit");
        buttons.add(editPriceButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(product);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(600, 400);

        exitButton.addActionListener(e -> setProductDashboard());
        editPriceButton.addActionListener(e -> {
            if (marketPlace.changeProductPrice(productField.getText())) {
                setProductDashboard();
            }
        });
    }

    public static void setDeleteProduct() {
        frame.getContentPane().removeAll();
        frame.setName("Delete Product");

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(130, 0, 160, 0));

        JLabel label = new JLabel("Are you sure you want to remove this product from the store?");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 20));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton removeProductButton = new JButton("Remove Product");
        removeProductButton.setForeground(Color.RED);
        JButton exitButton = new JButton("Exit");
        buttons.add(removeProductButton);
        buttons.add(exitButton);

        panel.add(label);
        panel.add(buttons);

        frame.add(panel);
        initializeFrame(800, 500);

        exitButton.addActionListener(e -> setProductDashboard());
        removeProductButton.addActionListener(e -> {
            marketPlace.deleteProduct();
            setStoreDashboard();
        });
    }

    public static void setViewStoreStatisticsByCustomer(int sort) {
        frame.getContentPane().removeAll();
        frame.setName("Store Customer Statistics");

        String[] customerStats = marketPlace.getStoreCustomerStats().split("~");

        ArrayList<Pair<String, Integer>> pairList = new ArrayList<>();
        for (String s : customerStats) {
            String[] statsDesc = s.split(",");
            pairList.add(new Pair<>(statsDesc[0], Integer.parseInt(statsDesc[1])));
        }

        if (sort == 1 || sort == 2) {
            pairList.sort(Comparator.comparing(pair -> pair.second));
            if (sort == 2) {
                Collections.reverse(pairList);
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Store Customer Statistics");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (Pair<String, Integer> p : pairList) {
            JLabel statsLabel = new JLabel(p.first + " bought " + p.second + " items from this store");
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(statsLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomButtons = new JPanel(new FlowLayout());

        String[] sortChoices = {"Default", "Low to high", "High to low"};
        int placeholder;
        if (sort != 0) {
            sortChoices = Arrays.copyOfRange(sortChoices, 1, sortChoices.length);
            placeholder = 1;
        } else {
            placeholder = 0;
        }

        JComboBox<String> sortComboBox = new JComboBox<>(sortChoices);
        sortComboBox.setSelectedIndex(sort - placeholder);
        bottomButtons.add(sortComboBox);

        JButton submitSort = new JButton("Submit");
        bottomButtons.add(submitSort);

        JLabel space = new JLabel("     ");
        bottomButtons.add(space);

        JButton exitButton = new JButton("Exit");
        bottomButtons.add(exitButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomButtons, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        submitSort.addActionListener(e ->
                setViewStoreStatisticsByCustomer(sortComboBox.getSelectedIndex() + placeholder));
        exitButton.addActionListener(e -> setViewStoreStatistics());
    }

    public static void setViewStoreStatisticsByProduct(int sort) {
        frame.getContentPane().removeAll();
        frame.setName("Store Product Statistics");

        String[] storeStatsByProduct = marketPlace.getProductStats().split("~");

        ArrayList<Pair<String, Integer>> pairList = new ArrayList<>();
        for (String s : storeStatsByProduct) {
            String[] storeStatsDesc = s.split(",");
            pairList.add(new Pair<>(storeStatsDesc[0], Integer.parseInt(storeStatsDesc[1])));
        }

        if (sort == 1 || sort == 2) {
            pairList.sort(Comparator.comparing(pair -> pair.second));
            if (sort == 2) {
                Collections.reverse(pairList);
            }
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel label = new JLabel("Store Product Statistics");
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (Pair<String, Integer> p : pairList) {
            String str = String.format("%s was purchased %d times", p.first, p.second);
            JLabel statsLabel = new JLabel(str);
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(statsLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomButtons = new JPanel(new FlowLayout());

        String[] sortChoices = {"Default", "Low to high", "High to low"};
        int placeholder;
        if (sort != 0) {
            sortChoices = Arrays.copyOfRange(sortChoices, 1, sortChoices.length);
            placeholder = 1;
        } else {
            placeholder = 0;
        }

        JComboBox<String> sortComboBox = new JComboBox<>(sortChoices);
        sortComboBox.setSelectedIndex(sort - placeholder);
        bottomButtons.add(sortComboBox);

        JButton submitSort = new JButton("Submit");
        bottomButtons.add(submitSort);

        JLabel space = new JLabel("     ");
        bottomButtons.add(space);

        JButton exitButton = new JButton("Exit");
        bottomButtons.add(exitButton);

        frame.add(label, BorderLayout.NORTH);
        frame.add(scrollPane);
        frame.add(bottomButtons, BorderLayout.SOUTH);

        initializeFrame(1000, 700);

        submitSort.addActionListener(e ->
                setViewStoreStatisticsByProduct(sortComboBox.getSelectedIndex() + placeholder));
        exitButton.addActionListener(e -> setViewStoreStatistics());
    }

    public static void setViewProduct(int sort, String searchBy, String searchFor) {
        frame.getContentPane().removeAll();
        frame.setName("View Product");

        String[] product = marketPlace.getProduct().split(",");

        JPanel panel = new JPanel(new GridLayout(6, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 80, 110, 80));
        JLabel label = new JLabel(product[2]);
        label.setHorizontalAlignment(0);
        label.setFont(new Font("SANS_SERIF", Font.BOLD, 25));

        JLabel productDescription = new JLabel("Description: " + product[3]);
        productDescription.setHorizontalAlignment(0);
        JLabel productQuantity = new JLabel("Available Quantity: " + product[4]);
        productQuantity.setHorizontalAlignment(0);
        String price = String.format("%.2f", Double.parseDouble(product[5]));
        JLabel productPrice = new JLabel("Price: $" + price);
        productPrice.setHorizontalAlignment(0);

        JPanel buttons = new JPanel(new FlowLayout());
        int quantityOfProduct = Integer.parseInt(product[4]);
        if (Integer.parseInt(product[4]) > 99) {
            quantityOfProduct = 99;
        }
        String[] quantityToAdd = new String[quantityOfProduct];
        for (int i = 1; i <= quantityOfProduct; i++) {
            quantityToAdd[i - 1] = "" + i;
        }
        JComboBox quantityOfProductToAdd = new JComboBox<>(quantityToAdd);
        quantityOfProductToAdd.setSelectedItem(0);
        JButton addProduct = new JButton("Add to Cart");
        buttons.add(quantityOfProductToAdd);
        buttons.add(addProduct);

        JButton exitButton = new JButton("Exit");

        panel.add(label);
        panel.add(productDescription);
        panel.add(productQuantity);
        panel.add(productPrice);
        panel.add(buttons);
        panel.add(exitButton);

        frame.add(panel);
        initializeFrame(700, 500);

        exitButton.addActionListener(e -> setViewItems(marketPlace.getItemsByCategory(searchBy, searchFor),
                sort, searchBy, searchFor));
        addProduct.addActionListener(e -> {
            marketPlace.addItemToCart(Integer.parseInt(quantityOfProductToAdd.getSelectedItem().toString()));
            setViewItems(marketPlace.getItemsByCategory(searchBy, searchFor), sort, searchBy, searchFor);
        });
    }
}