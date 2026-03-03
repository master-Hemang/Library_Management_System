package factory;

import models.Book;
import models.LibraryItem;

public class LibraryItemFactory {

    public static LibraryItem createItem(String type, String... params) {
        switch(type.toLowerCase()) {
            case "book":
                return createBook(params);
            default:
                throw new IllegalArgumentException("Unknown item type: " + type);
        }
    }

    private static Book createBook(String... params) {
        if (params.length < 4) {
            throw new IllegalArgumentException("Insufficient parameters for book");
        }
        Book book = new Book(params[0], params[1], params[2],
                Integer.parseInt(params[3]));
        if (params.length > 4) book.setGenre(params[4]);
        return book;
    }
}