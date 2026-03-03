package strategy;

import models.LibraryItem;
import java.util.List;
import java.util.stream.Collectors;

public class ISBNSearchStrategy implements SearchStrategy {
    @Override
    public List<LibraryItem> search(String query, List<LibraryItem> items) {
        return items.stream()
                .filter(item -> item.getIsbn().equals(query))
                .collect(Collectors.toList());
    }
}