package models;

import java.util.ArrayList;
import java.util.List;

public class Patron extends Person {
    private List<Transaction> borrowingHistory;
    private List<String> preferences;
    private int maxBorrowLimit = 5;
    private List<LibraryItem> currentlyBorrowed;

    public Patron(String name, String email) {
        super(name, email);
        this.borrowingHistory = new ArrayList<>();
        this.preferences = new ArrayList<>();
        this.currentlyBorrowed = new ArrayList<>();
    }

    public void borrowItem(LibraryItem item) {
        if (currentlyBorrowed.size() < maxBorrowLimit) {
            currentlyBorrowed.add(item);
        } else {
            throw new IllegalStateException("Borrowing limit exceeded");
        }
    }

    public void returnItem(LibraryItem item) {
        currentlyBorrowed.remove(item);
    }

    public List<Transaction> getBorrowingHistory() { return borrowingHistory; }
    public List<String> getPreferences() { return preferences; }
    public void addPreference(String preference) { this.preferences.add(preference); }
    public List<LibraryItem> getCurrentlyBorrowed() { return currentlyBorrowed; }
}