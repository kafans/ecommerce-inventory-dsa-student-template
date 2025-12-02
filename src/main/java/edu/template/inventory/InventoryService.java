
package edu.template.inventory;

import edu.template.inventory.ds.HashTable;
import edu.template.inventory.ds.SinglyLinkedList;
import edu.template.inventory.ds.Sorting;
import edu.template.inventory.model.Product;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class InventoryService {

    private final SinglyLinkedList<Product> products = new SinglyLinkedList<>();
    private final HashTable<String, Product> bySku = new HashTable<>();
    private final HashTable<String, Product> byName = new HashTable<>();

    private final List<Product> fallback = new ArrayList<>();

    public void load(List<Product> list) {
        fallback.clear();
        fallback.addAll(list);
        try {
            for (Product p : list) products.addLast(p);
            rebuildIndexes();
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("(info) Using fallback storage: " + ex.getMessage());
        }
    }

    public boolean add(Product p) {
        try {
            products.addLast(p);
            bySku.put(p.getSku(), p);
            byName.put(p.getName(), p);
            return true;
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
            fallback.add(p);
            return true;
        }
    }

    public boolean removeBySku(String sku) {
        try {
            int idx = 0;
            for (Product p : products) {
                if (p.getSku().equals(sku)) {
                    products.removeAt(idx);
                    bySku.remove(sku);
                    byName.remove(p.getName());
                    return true;
                }
                idx++;
            }
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
        }
        for (int i=0;i<fallback.size();i++) if (fallback.get(i).getSku().equals(sku)) { fallback.remove(i); return true; }
        return false;
    }

    public boolean updatePrice(String sku, double newPrice) {
        try {
            Product p = bySku.get(sku);
            if (p == null) return false;
            p.setPrice(newPrice);
            return true;
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
            for (Product p : fallback) if (p.getSku().equals(sku)) { p.setPrice(newPrice); return true; }
            return false;
        }
    }

    public List<Product> list(String by) {
        List<Product> items = new ArrayList<>(fallback);
        try {
            Comparator<Product> cmp;
            switch (by) {
                case "price": cmp = Comparator.comparingDouble(Product::getPrice); break;
                case "stock": cmp = Comparator.comparingInt(Product::getStock); break;
                default: cmp = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            }
            Product[] arr = items.toArray(new Product[0]);
            Sorting.mergeSort(arr, cmp);
            return Arrays.asList(arr);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage() + " — showing unsorted list.");
            return items;
        }
    }

    public Product find(String key) {
        try {
            Product p = bySku.get(key);
            if (p != null) return p;
            return byName.get(key);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage() + " — search disabled until implemented.");
            return null;
        }
    }

    public boolean restock(String sku, int qty) {
        if (qty <= 0) return false;
        try {
            Product p = bySku.get(sku);
            if (p == null) return false;
            p.setStock(p.getStock() + qty);
            return true;
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
            for (Product p : fallback) if (p.getSku().equals(sku)) { p.setStock(p.getStock() + qty); return true; }
            return false;
        }
    }

    public static final class OrderResult {
        public final boolean ok;
        public final String message;
        OrderResult(boolean ok, String message) { this.ok = ok; this.message = message; }
    }

    public OrderResult order(String sku, int qty) {
        if (qty <= 0) return new OrderResult(false, "Quantity must be > 0");
        try {
            Product p = bySku.get(sku);
            if (p == null) return new OrderResult(false, "SKU not found");
            if (p.getStock() < qty) return new OrderResult(false, "Insufficient stock");
            int remaining = p.getStock() - qty;
            p.setStock(remaining);
            if (remaining == 0) {
                removeBySku(sku);
                return new OrderResult(true, "Order placed. Item is now out of stock and removed from inventory.");
            }
            return new OrderResult(true, "Order placed. Remaining stock: " + remaining);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
            for (Product p : fallback) if (p.getSku().equals(sku)) {
                if (p.getStock() < qty) return new OrderResult(false, "Insufficient stock");
                int remaining = p.getStock() - qty;
                p.setStock(remaining);
                if (remaining == 0) {
                    for (int i=0;i<fallback.size();i++) if (fallback.get(i).getSku().equals(sku)) { fallback.remove(i); break; }
                    return new OrderResult(true, "Order placed. (fallback) Removed from inventory.");
                }
                return new OrderResult(true, "Order placed. (fallback) Remaining stock: " + remaining);
            }
            return new OrderResult(false, "SKU not found (fallback)");
        }
    }

    public List<Product> lowStock(int threshold) {
        List<Product> res = new ArrayList<>();
        for (Product p : fallback) if (p.getStock() <= threshold) res.add(p);
        try {
            Product[] arr = res.toArray(new Product[0]);
            Sorting.selectionSort(arr, Comparator.comparingInt(Product::getStock));
            return Arrays.asList(arr);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage() + " — showing unsorted low-stock report.");
            return res;
        }
    }

    public void saveCsv(String path) throws IOException {
        Path p = Paths.get(path);
        Files.createDirectories(p.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            bw.write("sku,name,category,price,stock\n");
            for (Product pr : fallback) {
                bw.write(String.format("%s,%s,%s,%.2f,%d%n",
                        pr.getSku(), pr.getName(), pr.getCategory(), pr.getPrice(), pr.getStock()));
            }
        }
    }

    private void rebuildIndexes() {
        try {
            for (Product p : fallback) {
                bySku.put(p.getSku(), p);
                byName.put(p.getName(), p);
            }
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
        }
    }
}
