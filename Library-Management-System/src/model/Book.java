
package model;

public class Book extends Document {
    private String publisher;

    public Book(String id, String title, String author,
                int quantity, String publisher) {
        super(id, title, author, quantity);
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public String getDocumentType() {
        return "Sách";
    }
}
