import java.util.Scanner;

interface Payable {
    void makePayment(double amount);
}


class Product {
    protected int productId;
    protected String productName;
    protected double price;

    Product(int productId, String productName, double price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    public void displayProduct() {
        System.out.println(productId + "  " + productName + "  Rs." + price);
    }

    public double calculatePrice() {
        return price;
    }
}


class Electronics extends Product {
    private int warrantyPeriod;

    Electronics(int productId, String productName, double price, int warrantyPeriod) {
        super(productId, productName, price);
        this.warrantyPeriod = warrantyPeriod;
    }


    @Override
    public double calculatePrice() {
        return price + 200; // extra service charge
    }
}


class Clothing extends Product {
    private String size;

    Clothing(int productId, String productName, double price, String size) {
        super(productId, productName, price);
        this.size = size;
    }
}


class Order implements Payable {
    private double totalAmount = 0;

    public void addProduct(Product product) {
        totalAmount += product.calculatePrice();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public void makePayment(double amount) {
        System.out.println("Payment of Rs." + amount + " successful.");
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Product p1 = new Electronics(1, "Mobile Phone", 10000, 1);
        Product p2 = new Clothing(2, "T-Shirt", 500, "M");


        System.out.println("Available Products:");
        p1.displayProduct();
        p2.displayProduct();

        Order order = new Order();
        order.addProduct(p1);
        order.addProduct(p2);


        System.out.println("Total Amount: Rs." + order.getTotalAmount());


        order.makePayment(order.getTotalAmount());

        System.out.println("Order Completed Successfully.");

        sc.close();
    }
}
