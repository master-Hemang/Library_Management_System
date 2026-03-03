package strategy;

import models.LibraryItem;
import java.util.List;
import java.util.stream.Collectors;

public class TitleSearchStrategy implements SearchStrategy {
    @Override
    public List<LibraryItem> search(String query, List<LibraryItem> items) {
        return items.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}