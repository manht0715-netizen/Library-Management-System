
package controller;

import model.Document;
import model.Reader;
import model.BorrowRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibraryManager implements Manageable<Document> {

    private List<Document> documents = new ArrayList<>();
    private List<Reader> readers = new ArrayList<>();
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    // =========================
    // QUAN LY TAI LIEU
    // =========================

    @Override
    public void add(Document item) {
        documents.add(item);
    }

    @Override
    public void update(Document item) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equals(item.getId())) {
                documents.set(i, item);
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        documents.removeIf(d -> d.getId().equals(id));
    }

    @Override
    public Document findById(String id) {
        for (Document d : documents) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List<Document> getAll() {
        return documents;
    }

    // TIM KIEM SACH THEO MA HOAC TEN
    public List<Document> searchDocuments(String keyword) {

        List<Document> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String searchText = keyword.trim().toLowerCase(Locale.ROOT);

        for (Document document : documents) {

            String id = document.getId().toLowerCase(Locale.ROOT);
            String title = document.getTitle().toLowerCase(Locale.ROOT);

            if (id.contains(searchText) || title.contains(searchText)) {
                results.add(document);
            }
        }

        return results;
    }

    // =========================
    // QUAN LY DOC GIA
    // =========================

    public void addReader(Reader reader) {
        readers.add(reader);
    }

    public void deleteReader(String id) {
        readers.removeIf(r -> r.getId().equals(id));
    }

    public Reader findReaderById(String id) {
        for (Reader reader : readers) {
            if (reader.getId().equals(id)) {
                return reader;
            }
        }
        return null;
    }

    public List<Reader> getReaders() {
        return readers;
    }

    // =========================
    // QUAN LY MUON SACH
    // =========================

    public boolean borrowDocument(
            String recordId,
            String readerId,
            String documentId) {

        Reader reader = findReaderById(readerId);
        Document document = findById(documentId);

        if (reader == null || document == null) {
            return false;
        }

        if (document.getQuantity() <= 0) {
            return false;
        }

        for (BorrowRecord record : borrowRecords) {
            if (record.getId().equals(recordId)) {
                return false;
            }
        }

        BorrowRecord record = new BorrowRecord(
                recordId,
                reader,
                document,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        borrowRecords.add(record);
        document.setQuantity(document.getQuantity() - 1);

        return true;
    }

    // =========================
    // QUAN LY TRA SACH
    // =========================

    public boolean returnDocument(String recordId) {

        for (BorrowRecord record : borrowRecords) {

            if (record.getId().equals(recordId)
                    && !record.isReturned()) {

                record.returnDocument();

                Document document = record.getDocument();

                document.setQuantity(
                        document.getQuantity() + 1
                );

                return true;
            }
        }

        return false;
    }

    // =========================
    // DANH SACH PHIEU MUON
    // =========================

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void addBorrowRecord(BorrowRecord record) {
        borrowRecords.add(record);
    }
}
