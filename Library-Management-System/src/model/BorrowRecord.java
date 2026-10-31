package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private String id;
    private Reader reader;
    private Document document;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    // Mức phạt: 5.000 đồng cho mỗi ngày quá hạn
    public static final long FINE_PER_DAY = 5000;

    public BorrowRecord(String id, Reader reader,
                        Document document,
                        LocalDate borrowDate,
                        LocalDate dueDate) {
        this.id = id;
        this.reader = reader;
        this.document = document;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getId() {
        return id;
    }

    public Reader getReader() {
        return reader;
    }

    public Document getDocument() {
        return document;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void returnDocument() {
        this.returnDate = LocalDate.now();
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    // Tính số ngày trả trễ
    public long getOverdueDays() {
        LocalDate actualReturnDate =
                (returnDate != null) ? returnDate : LocalDate.now();

        if (actualReturnDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, actualReturnDate);
        }

        return 0;
    }

    // Tính tổng tiền phạt
    public long getFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }
}