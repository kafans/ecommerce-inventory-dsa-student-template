
package edu.template.inventory.model;

import java.util.Objects;

public class Product implements Comparable<Product> {
    private final String sku;
    private String name;
    private String category;
    private double price;
    private int stock;

    public Product(String sku, String name, String category, double price, int stock) {
        this.sku = Objects.requireNonNull(sku);
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.price = price;
        this.stock = stock;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    @Override public String toString() {
        return String.format("%s | %s | %s | $%.2f | stock=%d", sku, name, category, price, stock);
    }

    @Override public int compareTo(Product o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
