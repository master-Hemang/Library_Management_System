package models;

public class Book extends LibraryItem {
    private String author;
    private String publisher;
    private String genre;
    private int pageCount;

    public Book(String title, String author, String isbn, int publicationYear) {
        super(title, isbn, publicationYear);
        this.author = author;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
}