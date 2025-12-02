
package edu.template.inventory.io;

import edu.template.inventory.model.Product;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DataGenerator {
    private static final String[] CATEGORIES = {"Accessories","Storage","Displays","Cameras","Home","Audio","Gaming","Networking"};
    private static final String[] ADJ = {"Pro","Lite","Max","Mini","Plus","Ultra","Air","Smart"};
    private static final String[] NOUN = {"Mouse","Keyboard","Headset","Charger","Hub","Router","Speaker","Lamp","SSD","HDD","Monitor","Webcam","Tablet","Laptop"};

    public static List<Product> generate(int n, long seed) {
        Random rnd = new Random(seed);
        List<Product> out = new ArrayList<>(n);
        for (int i=0;i<n;i++) {
            String sku = "SKU-" + (1000 + i);
            String name = ADJ[rnd.nextInt(ADJ.length)] + " " + NOUN[rnd.nextInt(NOUN.length)];
            String cat = CATEGORIES[rnd.nextInt(CATEGORIES.length)];
            double price = Math.round((5 + rnd.nextDouble()*295) * 100.0) / 100.0;
            int stock = 1 + rnd.nextInt(50);
            out.add(new Product(sku, name, cat, price, stock));
        }
        return out;
    }

    public static void save(List<Product> items, String path) throws IOException {
        Path p = Paths.get(path);
        Files.createDirectories(p.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            bw.write("sku,name,category,price,stock\n");
            for (Product pr : items) {
                bw.write(String.format("%s,%s,%s,%.2f,%d%n",
                        pr.getSku(), pr.getName(), pr.getCategory(), pr.getPrice(), pr.getStock()));
            }
        }
    }
}
