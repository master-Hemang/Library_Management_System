package service;

import models.*;
import observer.*;
import strategy.*;
import factory.LibraryItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibraryManagementSystem {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementSystem.class);

    private Map<String, LibraryBranch> branches;
    private Map<String, Patron> patrons;
    private Map<String, Transaction> transactions;
    private NotificationService notificationService;
    private Queue<Reservation> reservationQueue;
    private SearchStrategy searchStrategy;
    private RecommendationStrategy recommendationStrategy;

    private static LibraryManagementSystem instance;

    private LibraryManagementSystem() {
        this.branches = new ConcurrentHashMap<>();
        this.patrons = new ConcurrentHashMap<>();
        this.transactions = new ConcurrentHashMap<>();
        this.notificationService = new NotificationService();
        this.reservationQueue = new LinkedList<>();
        this.searchStrategy = new TitleSearchStrategy();
        this.recommendationStrategy = new GenreBasedRecommendationStrategy();

        logger.info("Library Management System initialized");
    }

    public static synchronized LibraryManagementSystem getInstance() {
        if (instance == null) {
            instance = new LibraryManagementSystem();
        }
        return instance;
    }

    public void addBranch(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
        logger.info("Branch added: {}", branch.getName());
    }

    public LibraryBranch getBranch(String branchId) {
        return branches.get(branchId);
    }

    public void registerPatron(Patron patron) {
        patrons.put(patron.getId(), patron);
        logger.info("Patron registered: {}", patron.getName());
    }

    public Patron getPatron(String patronId) {
        return patrons.get(patronId);
    }

    public void updatePatronInfo(String patronId, Patron updatedInfo) {
        Patron existing = patrons.get(patronId);
        if (existing != null) {
            existing.setName(updatedInfo.getName());
            existing.setEmail(updatedInfo.getEmail());
            existing.setPhone(updatedInfo.getPhone());
            existing.setAddress(updatedInfo.getAddress());
            logger.info("Patron updated: {}", patronId);
        }
    }

    public void addBookToBranch(String branchId, Book book, int quantity) {
        LibraryBranch branch = branches.get(branchId);
        if (branch != null) {
            branch.addItem(book, quantity);
            logger.info("Book added to branch {}: {} (Quantity: {})",
                    branch.getName(), book.getTitle(), quantity);
        }
    }

    public void removeBookFromBranch(String branchId, String bookId) {
        LibraryBranch branch = branches.get(branchId);
        if (branch != null) {
            branch.removeItem(bookId);
            logger.info("Book removed from branch {}: {}", branch.getName(), bookId);
        }
    }

    public List<LibraryItem> searchBooks(String query, String searchType) {
        switch(searchType.toLowerCase()) {
            case "title":
                setSearchStrategy(new TitleSearchStrategy());
                break;
            case "author":
                setSearchStrategy(new AuthorSearchStrategy());
                break;
            case "isbn":
                setSearchStrategy(new ISBNSearchStrategy());
                break;
            default:
                setSearchStrategy(new TitleSearchStrategy());
        }

        List<LibraryItem> allItems = branches.values().stream()
                .flatMap(branch -> branch.getInventory().values().stream())
                .collect(Collectors.toList());

        return searchStrategy.search(query, allItems);
    }

    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    public synchronized Transaction checkoutItem(String patronId, String itemId, String branchId) {
        Patron patron = patrons.get(patronId);
        LibraryBranch branch = branches.get(branchId);

        if (patron == null || branch == null) {
            logger.error("Checkout failed: Invalid patron or branch");
            throw new IllegalArgumentException("Invalid patron or branch");
        }

        if (!branch.hasItem(itemId)) {
            logger.error("Checkout failed: Item not available");
            throw new IllegalStateException("Item not available");
        }

        LibraryItem item = branch.getInventory().get(itemId);

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            logger.error("Checkout failed: Item is not available for checkout");
            throw new IllegalStateException("Item is not available");
        }

        Transaction transaction = new Transaction(patron, item, branch);

        item.setStatus(ItemStatus.BORROWED);
        patron.borrowItem(item);
        transactions.put(transaction.getTransactionId(), transaction);
        patron.getBorrowingHistory().add(transaction);

        logger.info("Item checked out: {} by patron {}",
                item.getTitle(), patron.getName());

        checkReservations(item);

        return transaction;
    }

    public synchronized Transaction returnItem(String transactionId) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            logger.error("Return failed: Transaction not found");
            throw new IllegalArgumentException("Transaction not found");
        }

        transaction.setReturnDate(LocalDateTime.now());
        transaction.setType(TransactionType.RETURN);

        LibraryItem item = transaction.getItem();
        item.setStatus(ItemStatus.AVAILABLE);

        Patron patron = transaction.getPatron();
        patron.returnItem(item);

        logger.info("Item returned: {} by patron {}",
                item.getTitle(), patron.getName());

        notificationService.notifyObservers(item.getId(),
                "The item '" + item.getTitle() + "' you reserved is now available.");

        return transaction;
    }

    public void reserveItem(String patronId, String itemId) {
        Patron patron = patrons.get(patronId);

        LibraryItem item = findItemAcrossBranches(itemId);

        if (item == null) {
            logger.error("Reservation failed: Item not found");
            throw new IllegalArgumentException("Item not found");
        }

        if (item.getStatus() == ItemStatus.AVAILABLE) {
            logger.info("Item is available, please checkout instead");
            return;
        }

        Reservation reservation = new Reservation(patron, item);
        reservationQueue.add(reservation);

        notificationService.subscribe(itemId,
                new PatronNotificationObserver(patron));

        logger.info("Item reserved: {} by patron {}",
                item.getTitle(), patron.getName());
    }

    private LibraryItem findItemAcrossBranches(String itemId) {
        return branches.values().stream()
                .map(branch -> branch.getInventory().get(itemId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private void checkReservations(LibraryItem item) {
        List<Reservation> pendingReservations = reservationQueue.stream()
                .filter(r -> r.getItem().equals(item) &&
                        r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());

        if (!pendingReservations.isEmpty()) {
            item.setStatus(ItemStatus.RESERVED);
        }
    }

    public void transferItem(String itemId, String fromBranchId, String toBranchId) {
        LibraryBranch fromBranch = branches.get(fromBranchId);
        LibraryBranch toBranch = branches.get(toBranchId);

        if (fromBranch == null || toBranch == null) {
            logger.error("Transfer failed: Invalid branch");
            throw new IllegalArgumentException("Invalid branch");
        }

        LibraryItem item = fromBranch.getInventory().get(itemId);

        if (item == null) {
            logger.error("Transfer failed: Item not found in source branch");
            throw new IllegalArgumentException("Item not found");
        }

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            logger.error("Transfer failed: Item not available for transfer");
            throw new IllegalStateException("Item not available");
        }

        item.setStatus(ItemStatus.IN_TRANSIT);
        fromBranch.removeItem(itemId);
        toBranch.addItem(item, 1);
        item.setCurrentBranch(toBranch);
        item.setStatus(ItemStatus.AVAILABLE);

        logger.info("Item transferred from {} to {}",
                fromBranch.getName(), toBranch.getName());
    }

    public List<LibraryItem> getRecommendationsForPatron(String patronId) {
        Patron patron = patrons.get(patronId);

        if (patron == null) {
            logger.error("Recommendations failed: Patron not found");
            throw new IllegalArgumentException("Patron not found");
        }

        List<LibraryItem> availableItems = branches.values().stream()
                .flatMap(branch -> branch.getInventory().values().stream())
                .filter(item -> item.getStatus() == ItemStatus.AVAILABLE)
                .collect(Collectors.toList());

        return recommendationStrategy.getRecommendations(patron, availableItems);
    }

    public void setRecommendationStrategy(RecommendationStrategy strategy) {
        this.recommendationStrategy = strategy;
    }

    public Map<String, Object> generateLibraryReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("totalBranches", branches.size());
        report.put("totalPatrons", patrons.size());

        int totalBooks = branches.values().stream()
                .mapToInt(b -> b.getInventory().size())
                .sum();
        report.put("totalBooks", totalBooks);

        long borrowedBooks = branches.values().stream()
                .flatMap(b -> b.getInventory().values().stream())
                .filter(item -> item.getStatus() == ItemStatus.BORROWED)
                .count();
        report.put("borrowedBooks", borrowedBooks);

        long overdueTransactions = transactions.values().stream()
                .filter(Transaction::isOverdue)
                .count();
        report.put("overdueItems", overdueTransactions);

        return report;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }
}