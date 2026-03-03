package models;

import java.util.Objects;
import java.util.UUID;

public abstract class LibraryItem {
    private final String id;
    private String title;
    private String isbn;
    private int publicationYear;
    private ItemStatus status;
    private LibraryBranch currentBranch;

    public LibraryItem(String title, String isbn, int publicationYear) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.status = ItemStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public LibraryBranch getCurrentBranch() { return currentBranch; }
    public void setCurrentBranch(LibraryBranch branch) { this.currentBranch = branch; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryItem that = (LibraryItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Title: %s, ISBN: %s, Status: %s]",
                getClass().getSimpleName(), id, title, isbn, status);
    }
}