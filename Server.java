import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server for the marketplace
 * Every time a new user connects, a new thread is created and run
 */

public class Server {
    static HashMap<String, Seller> sellerMap = new HashMap<>();
    static HashMap<String, Customer> customerMap = new HashMap<>();

    public static void serialize(Object obj1, Object obj2) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("map1.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(obj1);

            FileOutputStream fileOutputStream2 = new FileOutputStream("map2.txt");
            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(fileOutputStream2);
            objectOutputStream2.writeObject(obj2);

            System.out.println("Serialized");
        } catch (Exception e) {
            System.out.println("Unable to serialize");
        }
    }

    public static Object[] deserialize() {
        Object[] ret = new Object[2];
        try {
            FileInputStream fileInputStream = new FileInputStream("map1.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ret[0] = objectInputStream.readObject();

            FileInputStream fileInputStream2 = new FileInputStream("map2.txt");
            ObjectInputStream objectInputStream2 = new ObjectInputStream(fileInputStream2);
            ret[1] = objectInputStream2.readObject();

            System.out.println("Deserialized");
            return ret;
        } catch (Exception e) {
            System.out.println("Unable to deserialize");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        Object[] ds = deserialize();
        if (ds != null) {
            sellerMap = (HashMap<String, Seller>) ds[0];
            customerMap = (HashMap<String, Customer>) ds[1];
        }

        ServerSocket serverSocket = new ServerSocket(12345);
        while (true) { // continuously accept connections
            Socket socket = serverSocket.accept(); // accept connection from client
            ServerThread serverThread = new ServerThread(socket); // create new thread to handle client
            serverThread.start(); // spawn new thread and continue loop
            serialize(sellerMap, customerMap);
        }
    }

}



