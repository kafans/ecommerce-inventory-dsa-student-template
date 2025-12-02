
package edu.template.inventory;

import edu.template.inventory.io.CsvLoader;
import edu.template.inventory.io.DataGenerator;
import edu.template.inventory.model.Product;

import java.nio.file.*;
import java.util.*;
import java.io.*;

public class App {
    public static void main(String[] args) throws Exception {
        Map<String, String> argMap = parseArgs(args);
        String dataPath = argMap.getOrDefault("--data", "data/products.csv");

        InventoryService svc = new InventoryService();
        if (Files.exists(Paths.get(dataPath))) {
            List<Product> items = CsvLoader.load(dataPath);
            svc.load(items);
            System.out.println("Loaded " + items.size() + " products from " + dataPath);
        } else {
            System.out.println("Data file not found: " + dataPath + " (continuing with empty inventory)");
        }

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("=== E-Commerce Inventory Manager (Student Template) ===");
            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("Choose role:");
                System.out.println("  1) Inventory Admin");
                System.out.println("  2) Customer");
                System.out.println("  3) Performance Analyzer (sorting, search, linked list)");
                System.out.println("  4) Generate dataset (CSV)");
                System.out.println("  5) Help");
                System.out.println("  0) Exit");
                System.out.print("> ");
                if (!sc.hasNextLine()) { System.out.println("No input. Exiting."); break; }
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1": adminMenu(sc, svc); break;
                    case "2": customerMenu(sc, svc); break;
                    case "3": Analyzer.run(sc); break;
                    case "4": dataGenMenu(sc, svc); break;
                    case "5": printHelp(); break;
                    case "0": running = false; break;
                    default: System.out.println("Unknown option.");
                }
            }
        }
    }

    private static void adminMenu(Scanner sc, InventoryService svc) {
        for (;;) {
            System.out.println();
            System.out.println("[Inventory Admin]");
            System.out.println("  1) List products (by name/price/stock)");
            System.out.println("  2) Find product (sku or exact name)");
            System.out.println("  3) Add new product");
            System.out.println("  4) Update price");
            System.out.println("  5) Restock");
            System.out.println("  6) Remove product (by SKU)");
            System.out.println("  7) Low-stock report");
            System.out.println("  8) Save to CSV");
            System.out.println("  0) Back");
            System.out.print("> ");
            if (!sc.hasNextLine()) { System.out.println("No input. Returning."); return; }
            String c = sc.nextLine().trim();
            try {
                switch (c) {
                    case "1": {
                        System.out.print("Sort by [name|price|stock]: ");
                        String by = sc.hasNextLine()? sc.nextLine().trim() : "name";
                        svc.list(by).forEach(System.out::println);
                        break;
                    }
                    case "2": {
                        System.out.print("Key (sku or name): ");
                        String key = sc.hasNextLine()? sc.nextLine().trim() : "";
                        Product p = svc.find(key);
                        System.out.println(p == null? "(TODO) students/you need to code this part: HashTable.get" : p);
                        break;
                    }
                    case "3": {
                        System.out.print("SKU: "); String sku = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("Name: "); String name = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("Category: "); String cat = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("Price: "); double price = Double.parseDouble(sc.hasNextLine()? sc.nextLine() : "0");
                        System.out.print("Stock: "); int stock = Integer.parseInt(sc.hasNextLine()? sc.nextLine() : "0");
                        boolean ok = svc.add(new Product(sku, name, cat, price, stock));
                        System.out.println(ok ? "Added (fallback). Note: LinkedList must be implemented." : "SKU exists or operation not available.");
                        break;
                    }
                    case "4": {
                        System.out.print("SKU: "); String sku = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("New price: "); double price = Double.parseDouble(sc.hasNextLine()? sc.nextLine() : "0");
                        System.out.println(svc.updatePrice(sku, price) ? "Updated." : "SKU not found or operation not available.");
                        break;
                    }
                    case "5": {
                        System.out.print("SKU: "); String sku = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("Qty: "); int qty = Integer.parseInt(sc.hasNextLine()? sc.nextLine() : "0");
                        System.out.println(svc.restock(sku, qty) ? "OK" : "Fail (requires HashTable + LinkedList).");
                        break;
                    }
                    case "6": {
                        System.out.print("SKU: "); String sku = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.println(svc.removeBySku(sku) ? "Removed." : "Not found or operation not available.");
                        break;
                    }
                    case "7": {
                        System.out.print("Threshold (default 5): ");
                        String t = sc.hasNextLine()? sc.nextLine().trim() : "";
                        int thr = t.isEmpty()? 5 : Integer.parseInt(t);
                        svc.lowStock(thr).forEach(System.out::println);
                        break;
                    }
                    case "8": {
                        System.out.print("Path to save (e.g., data/out.csv): ");
                        String path = sc.hasNextLine()? sc.nextLine().trim() : "data/out.csv";
                        svc.saveCsv(path.isEmpty()? "data/out.csv" : path);
                        System.out.println("Saved.");
                        break;
                    }
                    case "0": return;
                    default: System.out.println("Unknown option.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void customerMenu(Scanner sc, InventoryService svc) {
        for (;;) {
            System.out.println();
            System.out.println("[Customer]");
            System.out.println("  1) Browse products");
            System.out.println("  2) Search (sku or name)");
            System.out.println("  3) Order (sku + qty)");
            System.out.println("  0) Back");
            System.out.print("> ");
            if (!sc.hasNextLine()) { System.out.println("No input. Returning."); return; }
            String c = sc.nextLine().trim();
            try {
                switch (c) {
                    case "1": svc.list("name").forEach(System.out::println); break;
                    case "2": {
                        System.out.print("Key: "); String key = sc.hasNextLine()? sc.nextLine() : "";
                        Product p = svc.find(key);
                        System.out.println(p == null? "(TODO) students/you need to code this part: HashTable.get" : p);
                        break;
                    }
                    case "3": {
                        System.out.print("SKU: "); String sku = sc.hasNextLine()? sc.nextLine() : "";
                        System.out.print("Qty: "); int qty = Integer.parseInt(sc.hasNextLine()? sc.nextLine() : "0");
                        InventoryService.OrderResult r = svc.order(sku, qty);
                        System.out.println(r.message);
                        break;
                    }
                    case "0": return;
                    default: System.out.println("Unknown option.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void dataGenMenu(Scanner sc, InventoryService svc) {
        System.out.print("How many items (e.g., 500): ");
        int n = Integer.parseInt(sc.hasNextLine()? sc.nextLine().trim() : "500");
        System.out.print("Output path (default data/generated.csv): ");
        String path = sc.hasNextLine()? sc.nextLine().trim() : "data/generated.csv";
        if (path.isEmpty()) path = "data/generated.csv";
        try {
            List<Product> gen = DataGenerator.generate(n, 42);
            DataGenerator.save(gen, path);
            System.out.println("Generated and saved " + n + " items to " + path);
            System.out.print("Load it now? [y/N]: ");
            String yn = sc.hasNextLine()? sc.nextLine().trim().toLowerCase() : "n";
            if (yn.equals("y")) {
                svc.load(gen);
                System.out.println("Inventory replaced with generated dataset.");
            }
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.getMessage());
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i=0;i<args.length;i++) {
            if (args[i].startsWith("--") && i+1<args.length) {
                m.put(args[i], args[i+1]); i++;
            }
        }
        return m;
    }

    private static void printHelp() {
        System.out.println(String.join("\n",
            "Your job is to implement LinkedList, HashTable, and Sorting.",
            "When you trigger features that rely on them, you'll see a TODO message until you implement.",
            "Performance Analyzer will skip benchmarks until your implementations are ready."
        ));
    }
}
