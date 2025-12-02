
# E-Commerce Inventory Manager — **Student Template**

This project runs and shows all menu options. **Core DS&A parts are intentionally unimplemented** so you can fill them in.

## What YOU must implement
- `src/main/java/edu/template/inventory/ds/SinglyLinkedList.java`
  - All list operations (add/remove/get/set/iterator/size)
  - **In-place merge sort on nodes** (method is provided; implement logic)
- `src/main/java/edu/template/inventory/ds/HashTable.java`
  - Separate chaining via your `SinglyLinkedList` + **rehashing**
  - `put/get/remove/containsKey/forEach` etc.
- `src/main/java/edu/template/inventory/ds/Sorting.java`
  - Selection, Merge, Quick sorts for arrays with a `Comparator`

**Rules (enforced by design)**  
- All **searching** (find by SKU/name) must use your `HashTable`.
- Inventory **storage** is a `SinglyLinkedList` and adding/removing must use it.
- Sorting in the app must use your `Sorting` algorithms (no `Arrays.sort`).

Until you implement these, the app will print:  
> `students/you need to code this part: <component>`

## You may read but shouldn't modify
- `App.java` (menus & roles)
- `InventoryService.java` (wires your DS&A, has fallbacks that just print messages)
- `Analyzer.java` (performance analyzer; skips benchmarks when DS&A is missing)
- `io/CsvLoader.java` (CSV input)
- `io/DataGenerator.java` (synthetic dataset)
- `model/Product.java`
- `build.gradle`, `settings.gradle`, `gradle.properties`

> If you change public method signatures in DS&A classes, **the app and analyzer will break**. Keep the API consistent.

## Run
```bash
./gradlew run --args="--data data/products.csv"
```

## Performance Analyzer
Menu → **Performance Analyzer** → generates a dataset and compares:
- Selection vs Merge vs Quick sort
- Linear search (linked list traversal) vs Hash search
- Linked-list ops (addFirst/addLast/removeAt(mid))

If a benchmark uses an unimplemented DS&A, it prints a TODO and skips.

## Data format
CSV with header: `sku,name,category,price,stock`  
Sample: `data/products.csv`

Good luck! 🚀
