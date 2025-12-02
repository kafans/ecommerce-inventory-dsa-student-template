
package edu.template.inventory;

import edu.template.inventory.ds.HashTable;
import edu.template.inventory.ds.SinglyLinkedList;
import edu.template.inventory.ds.Sorting;
import edu.template.inventory.io.DataGenerator;
import edu.template.inventory.model.Product;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public final class Analyzer {
    private Analyzer() {}

    public static void run(Scanner sc) {
        System.out.println("[Performance Analyzer] (will skip benchmarks for unimplemented parts)");
        System.out.print("Generate how many items (e.g., 2000): ");
        String s = sc.hasNextLine()? sc.nextLine().trim() : "2000";
        int n = Integer.parseInt(s);
        List<Product> data = DataGenerator.generate(n, 456);
        InventoryService svc = new InventoryService();
        svc.load(data);
        reportAll(svc, "data/analyzer_report.csv");
    }

    private static void reportAll(InventoryService svc, String csvPath) {
        List<Product> list = svc.list("name");
        if (list.isEmpty()) {
            System.out.println("No data to analyze.");
            return;
        }
        Product[] shuffled = list.toArray(new Product[0]);
        Collections.shuffle(Arrays.asList(shuffled), new Random(42));

        Map<String, Double> sortMs = new LinkedHashMap<>();
        try { sortMs.put("selection", timeSort(shuffled, "selection")); } catch (UnsupportedOperationException ex) { System.out.println("(TODO) " + ex.getMessage()); }
        try { sortMs.put("merge",     timeSort(shuffled, "merge")); }     catch (UnsupportedOperationException ex) { System.out.println("(TODO) " + ex.getMessage()); }
        try { sortMs.put("quick",     timeSort(shuffled, "quick")); }     catch (UnsupportedOperationException ex) { System.out.println("(TODO) " + ex.getMessage()); }

        HashTable<String, Product> bySku = new HashTable<>();
        try {
            for (Product p : list) bySku.put(p.getSku(), p);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
        }

        List<String> keys = new ArrayList<>();
        for (Product p : list) keys.add(p.getSku());
        Collections.shuffle(keys, new Random(7));
        int probes = Math.min(100, keys.size());
        keys = keys.subList(0, probes);

        try {
            double linearMs = timeLinearSearch(list, keys);
            System.out.printf("Linear search (100 lookups): %.3f ms%n", linearMs);
        } catch (Exception ex) {
            System.out.println("(info) Linear search skipped: " + ex.getMessage());
        }

        try {
            double hashMs = timeHashSearch(bySku, keys);
            System.out.printf("Hash search (100 lookups):   %.3f ms%n", hashMs);
        } catch (UnsupportedOperationException ex) {
            System.out.println("(TODO) " + ex.getMessage());
        }

        System.out.println("CSV export is available once sorts complete successfully.");
        try {
            saveCsv(csvPath, list.size(), sortMs);
            System.out.println("Saved partial CSV: " + csvPath);
        } catch (Exception e) {
            System.out.println("(info) CSV export skipped: sorting not implemented.");
        }
    }

    private static double timeSort(Product[] source, String algo) {
        Product[] a = Arrays.copyOf(source, source.length);
        Comparator<Product> byPrice = Comparator.comparingDouble(Product::getPrice);
        long t0 = System.nanoTime();
        switch (algo) {
            case "selection": Sorting.selectionSort(a, byPrice); break;
            case "merge": Sorting.mergeSort(a, byPrice); break;
            case "quick": Sorting.quickSort(a, byPrice); break;
        }
        long t1 = System.nanoTime();
        return (t1 - t0) / 1e6;
    }

    private static double timeLinearSearch(List<Product> list, List<String> keys) {
        long t0 = System.nanoTime();
        for (String k : keys) {
            boolean found = false;
            for (Product p : list) {
                if (p.getSku().equals(k)) { found = true; break; }
            }
            if (!found) throw new RuntimeException("Key not found in fallback list: " + k);
        }
        long t1 = System.nanoTime();
        return (t1 - t0) / 1e6;
    }

    private static double timeHashSearch(HashTable<String, Product> bySku, List<String> keys) {
        long t0 = System.nanoTime();
        for (String k : keys) {
            if (bySku.get(k) == null) throw new RuntimeException("Key not found in HashTable: " + k);
        }
        long t1 = System.nanoTime();
        return (t1 - t0) / 1e6;
    }

    private static void saveCsv(String path, int n, Map<String, Double> sortMs) throws IOException {
        if (sortMs.isEmpty()) throw new IOException("no sort data");
        Path p = Paths.get(path);
        Files.createDirectories(p.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            bw.write("dataset_size");
            for (String k : sortMs.keySet()) bw.write("," + k + "_ms");
            bw.write("\n");
            bw.write(Integer.toString(n));
            for (double v : sortMs.values()) bw.write(String.format(java.util.Locale.US, ",%.3f", v));
            bw.write("\n");
        }
    }
}
