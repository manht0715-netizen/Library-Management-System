
package model;

public class Thesis extends Document {
    private String university;

    public Thesis(String id, String title, String author,
                  int quantity, String university) {
        super(id, title, author, quantity);
        this.university = university;
    }

    public String getUniversity() {
        return university;
    }

    @Override
    public String getDocumentType() {
        return "Luận văn";
    }
}
