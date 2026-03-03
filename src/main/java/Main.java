import models.*;
import service.LibraryManagementSystem;
import strategy.AuthorSearchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LibraryManagementSystem lms = LibraryManagementSystem.getInstance();

        LibraryBranch downtown = new LibraryBranch("Downtown Library", "123 Main St");
        LibraryBranch uptown = new LibraryBranch("Uptown Library", "456 Oak Ave");

        lms.addBranch(downtown);
        lms.addBranch(uptown);

        Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald",
                "9780743273565", 1925);
        book1.setGenre("Fiction");

        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee",
                "9780061120084", 1960);
        book2.setGenre("Fiction");

        Book book3 = new Book("1984", "George Orwell",
                "9780451524935", 1949);
        book3.setGenre("Science Fiction");

        lms.addBookToBranch(downtown.getBranchId(), book1, 3);
        lms.addBookToBranch(downtown.getBranchId(), book2, 2);
        lms.addBookToBranch(uptown.getBranchId(), book3, 4);

        Patron patron1 = new Patron("John Doe", "john@email.com");
        patron1.addPreference("Fiction");

        Patron patron2 = new Patron("Jane Smith", "jane@email.com");
        patron2.addPreference("Science Fiction");

        lms.registerPatron(patron1);
        lms.registerPatron(patron2);

        System.out.println("\n=== Search by Title ===");
        List<LibraryItem> results = lms.searchBooks("great", "title");
        results.forEach(System.out::println);

        System.out.println("\n=== Search by Author ===");
        lms.setSearchStrategy(new AuthorSearchStrategy());
        results = lms.searchBooks("orwell", "author");
        results.forEach(System.out::println);

        System.out.println("\n=== Checkout Process ===");
        try {
            Transaction transaction = lms.checkoutItem(
                    patron1.getId(),
                    book1.getId(),
                    downtown.getBranchId()
            );
            System.out.println("Checked out: " + transaction.getItem().getTitle() +
                    " by " + patron1.getName());
        } catch (Exception e) {
            logger.error("Checkout failed: {}", e.getMessage());
        }

        System.out.println("\n=== Reservation System ===");
        lms.reserveItem(patron2.getId(), book1.getId());
        System.out.println("Item reserved for patron: " + patron2.getName());

        System.out.println("\n=== Recommendations ===");
        List<LibraryItem> recommendations = lms.getRecommendationsForPatron(patron1.getId());
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available");
        } else {
            recommendations.forEach(item ->
                    System.out.println("Recommended: " + item.getTitle()));
        }

        System.out.println("\n=== Library Report ===");
        var report = lms.generateLibraryReport();
        report.forEach((key, value) ->
                System.out.println(key + ": " + value));
    }
}