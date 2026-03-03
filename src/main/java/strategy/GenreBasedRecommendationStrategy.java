package strategy;

import models.LibraryItem;
import models.Patron;
import models.Book;
import models.Transaction;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GenreBasedRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<LibraryItem> getRecommendations(Patron patron, List<LibraryItem> availableItems) {
        Map<String, Integer> genreCount = new HashMap<>();

        patron.getBorrowingHistory().stream()
                .filter(t -> t.getItem() instanceof Book)
                .map(t -> (Book) t.getItem())
                .forEach(book -> {
                    genreCount.put(book.getGenre(),
                            genreCount.getOrDefault(book.getGenre(), 0) + 1);
                });

        String preferredGenre = genreCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (preferredGenre == null) {
            return List.of();
        }

        List<String> borrowedIsbns = patron.getBorrowingHistory().stream()
                .map(t -> t.getItem().getIsbn())
                .collect(Collectors.toList());

        return availableItems.stream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .filter(book -> book.getGenre() != null && book.getGenre().equals(preferredGenre))
                .filter(book -> !borrowedIsbns.contains(book.getIsbn()))
                .limit(5)
                .collect(Collectors.toList());
    }
}