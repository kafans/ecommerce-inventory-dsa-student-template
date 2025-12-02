
package edu.template.inventory.io;

import edu.template.inventory.model.Product;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class CsvLoader {
    public static List<Product> load(String path) throws IOException {
        List<Product> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line = br.readLine(); // header
            if (line == null) return out;
            while ((line = br.readLine()) != null) {
                String[] t = splitCsv(line);
                if (t.length < 5) continue;
                String sku = t[0];
                String name = t[1];
                String cat = t[2];
                double price = Double.parseDouble(t[3]);
                int stock = Integer.parseInt(t[4]);
                out.add(new Product(sku, name, cat, price, stock));
            }
        }
        return out;
    }

    private static String[] splitCsv(String line) {
        return line.split(",", -1);
    }
}
