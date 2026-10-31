
package model;

public class Magazine extends Document {
    private int issueNumber;

    public Magazine(String id, String title, String author,
                    int quantity, int issueNumber) {
        super(id, title, author, quantity);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public String getDocumentType() {
        return "Tạp chí";
    }
}
