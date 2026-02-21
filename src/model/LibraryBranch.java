package model;

import java.util.*;

public class LibraryBranch {
    private final String branchId;
    private final String name;
    private final Map<String, Integer> inventory;

    public LibraryBranch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
        this.inventory = new HashMap<>();
    }

    public String getBranchId() { return branchId; }
    public String getName() { return name; }

    public void addBook(String isbn, int quantity) {
        inventory.merge(isbn, quantity, Integer::sum);
    }

    public boolean removeBook(String isbn, int quantity) {
        Integer current = inventory.get(isbn);
        if (current == null || current < quantity) return false;
        if (current.equals(quantity)) inventory.remove(isbn);
        else inventory.put(isbn, current - quantity);
        return true;
    }

    public int getAvailableQuantity(String isbn) {
        return inventory.getOrDefault(isbn, 0);
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    @Override
    public String toString() {
        return String.format("Branch{id='%s', name='%s'}", branchId, name);
    }
}
