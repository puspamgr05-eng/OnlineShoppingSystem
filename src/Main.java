import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// ---------------- INTERFACES ----------------
interface OrderOperations {
    void placeOrder(Order order);
    void cancelOrder(int orderId);
}

interface Payment {
    void makePayment(double amount, String method) throws PaymentException;
}

// ---------------- CUSTOM EXCEPTION ----------------
class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

// ---------------- ABSTRACT CLASS ----------------
abstract class Person {
    protected int id;
    protected String name;
    protected String phone;

    public Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }
}

// ---------------- CUSTOMER ----------------
class Customer extends Person implements Payment {

    public Customer(int id, String name, String phone) {
        super(id, name, phone);
    }

    public void viewProducts(ArrayList<Product> products) {
        System.out.println("\n----- AVAILABLE CLOTHES -----");
        for (Product p : products) {
            System.out.println(p.getProductId() + ". " + p.getProductName()
                    + " (" + p.getGender() + ") - Rs " + p.getPrice());
        }
    }

    @Override
    public void makePayment(double amount, String method) throws PaymentException {
        if (amount <= 0) {
            throw new PaymentException("Invalid payment amount!");
        }
        System.out.println("Payment successful: Rs " + amount + " via " + method);
    }
}

// ---------------- PRODUCT ----------------
class Product {
    private int productId;
    private String productName;
    private String gender;
    private double price;

    public Product(int productId, String productName, String gender, double price) {
        this.productId = productId;
        this.productName = productName;
        this.gender = gender;
        this.price = price;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getGender() { return gender; }
    public double getPrice() { return price; }
}

// ---------------- ORDER ----------------
class Order {
    private int orderId;
    private ArrayList<Product> products = new ArrayList<>();

    public Order(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() { return orderId; }

    public void addProduct(Product product) {
        products.add(product);
    }

    public double calculateTotal() {
        double total = 0;
        for (Product p : products) total += p.getPrice();
        if (total > 10000) total *= 0.95; // 5% discount
        return total;
    }

    public String toFileString() {
        String data = "Order ID: " + orderId + " | Items: ";
        for (Product p : products) data += p.getProductName() + ", ";
        data += "| Final Total: Rs " + calculateTotal();
        return data;
    }
}

// ---------------- SHOP ----------------
class ClothingShop implements OrderOperations {
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();

    public void addProduct(Product product) { products.add(product); }
    public ArrayList<Product> getProducts() { return products; }

    @Override
    public void placeOrder(Order order) {
        orders.add(order);
        System.out.println("Order placed successfully.");
    }

    @Override
    public void cancelOrder(int orderId) {
        orders.removeIf(o -> o.getOrderId() == orderId);
        System.out.println("Order cancelled successfully.");
    }
}

// ---------------- FILE HANDLING ----------------
class FileManager {
    public static void saveOrder(Order order) {
        try (FileWriter fw = new FileWriter("cloth_orders.txt", true)) {
            fw.write(order.toFileString() + "\n");
            System.out.println("Order saved to file.");
        } catch (IOException e) {
            System.out.println("File handling error.");
        }
    }
}

// ---------------- MULTITHREADING ----------------
class OrderProcessor extends Thread {
    private Order order;
    public OrderProcessor(Order order) { this.order = order; }

    @Override
    public void run() {
        System.out.println("Processing order...");
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        System.out.println("Order processed. Final Amount: Rs " + order.calculateTotal());
    }
}

// ---------------- MAIN ----------------
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ClothingShop shop = new ClothingShop();

        // Add Products
        shop.addProduct(new Product(1, "Boys T-Shirt", "Boys", 1200));
        shop.addProduct(new Product(2, "Boys Jeans", "Boys", 2500));
        shop.addProduct(new Product(3, "Girls Kurti", "Girls", 1800));
        shop.addProduct(new Product(4, "Girls Saree", "Girls", 6000));
        shop.addProduct(new Product(5, "Boys Jacket", "Boys", 4500));
        shop.addProduct(new Product(6, "Girls Top", "Girls", 1500));

        Customer customer = new Customer(101, "Puspa", "98XXXXXXXX");
        customer.viewProducts(shop.getProducts());

        Order order = new Order(1);

        while (true) {
            System.out.print("\nEnter product number (0 to finish): ");
            int choice = sc.nextInt();
            if (choice == 0) break;

            boolean found = false;
            for (Product p : shop.getProducts()) {
                if (p.getProductId() == choice) {
                    order.addProduct(p);
                    System.out.println(p.getProductName() + " added.");
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("Invalid choice.");
        }

        shop.placeOrder(order);

        System.out.print("Enter payment method (Cash/Online): ");
        String paymentMethod = sc.next();

        try {
            customer.makePayment(order.calculateTotal(), paymentMethod);
        } catch (PaymentException e) {
            System.out.println(e.getMessage());
        }

        FileManager.saveOrder(order);

        OrderProcessor processor = new OrderProcessor(order);
        processor.start();
    }
}