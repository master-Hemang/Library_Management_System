package models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryBranch {
    private final String branchId;
    private String name;
    private String location;
    private Map<String, LibraryItem> inventory;
    private Map<String, Integer> quantity;

    public LibraryBranch(String name, String location) {
        this.branchId = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.inventory = new ConcurrentHashMap<>();
        this.quantity = new ConcurrentHashMap<>();
    }

    public void addItem(LibraryItem item, int qty) {
        inventory.put(item.getId(), item);
        quantity.put(item.getId(), quantity.getOrDefault(item.getId(), 0) + qty);
        item.setCurrentBranch(this);
    }

    public void removeItem(String itemId) {
        inventory.remove(itemId);
        quantity.remove(itemId);
    }

    public Optional<LibraryItem> findItemByIsbn(String isbn) {
        return inventory.values().stream()
                .filter(item -> item.getIsbn().equals(isbn))
                .findFirst();
    }

    public List<LibraryItem> findItemsByTitle(String title) {
        return inventory.values().stream()
                .filter(item -> item.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean hasItem(String itemId) {
        return inventory.containsKey(itemId) && quantity.getOrDefault(itemId, 0) > 0;
    }

    public String getBranchId() { return branchId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Map<String, LibraryItem> getInventory() { return inventory; }
}