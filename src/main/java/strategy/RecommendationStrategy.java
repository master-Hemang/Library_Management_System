package strategy;

import models.LibraryItem;
import models.Patron;
import java.util.List;

public interface RecommendationStrategy {
    List<LibraryItem> getRecommendations(Patron patron, List<LibraryItem> availableItems);
}