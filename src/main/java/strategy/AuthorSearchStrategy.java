package strategy;

import models.LibraryItem;
import models.Book;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorSearchStrategy implements SearchStrategy {
    @Override
    public List<LibraryItem> search(String query, List<LibraryItem> items) {
        return items.stream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .filter(book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}