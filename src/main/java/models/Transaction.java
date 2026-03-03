package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final Patron patron;
    private final LibraryItem item;
    private final LibraryBranch branch;
    private final LocalDateTime checkoutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private TransactionType type;

    public Transaction(Patron patron, LibraryItem item, LibraryBranch branch) {
        this.transactionId = UUID.randomUUID().toString();
        this.patron = patron;
        this.item = item;
        this.branch = branch;
        this.checkoutDate = LocalDateTime.now();
        this.dueDate = checkoutDate.plusDays(14);
        this.type = TransactionType.CHECKOUT;
    }

    public String getTransactionId() { return transactionId; }
    public Patron getPatron() { return patron; }
    public LibraryItem getItem() { return item; }
    public LocalDateTime getCheckoutDate() { return checkoutDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public boolean isOverdue() {
        return returnDate == null && LocalDateTime.now().isAfter(dueDate);
    }
}