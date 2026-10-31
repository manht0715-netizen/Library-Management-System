
package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import controller.LibraryManager;
import model.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryData {

    private static final String FILE_NAME = "library.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Cau truc du lieu JSON
    private static class LibrarySnapshot {
        List<DocumentData> documents = new ArrayList<>();
        List<ReaderData> readers = new ArrayList<>();
        List<BorrowData> borrowRecords = new ArrayList<>();
    }

    private static class DocumentData {
        String type;
        String id;
        String title;
        String author;
        int quantity;

        String publisher;
        int issueNumber;
        String university;
    }

    private static class ReaderData {
        String type;
        String id;
        String fullName;
        String phone;

        String studentId;
        String department;
    }

    private static class BorrowData {
        String id;
        String readerId;
        String documentId;
        String borrowDate;
        String dueDate;
        String returnDate;
    }

    // LUU DU LIEU
    public static void save(LibraryManager manager) {

        LibrarySnapshot snapshot = new LibrarySnapshot();

        // Luu tai lieu
        for (Document document : manager.getAll()) {

            DocumentData data = new DocumentData();

            data.id = document.getId();
            data.title = document.getTitle();
            data.author = document.getAuthor();
            data.quantity = document.getQuantity();
            data.type = document.getDocumentType();

            if (document instanceof Book) {
                data.publisher = ((Book) document).getPublisher();
            }

            if (document instanceof Magazine) {
                data.issueNumber =
                        ((Magazine) document).getIssueNumber();
            }

            if (document instanceof Thesis) {
                data.university =
                        ((Thesis) document).getUniversity();
            }

            snapshot.documents.add(data);
        }

        // Luu doc gia
        for (Reader reader : manager.getReaders()) {

            ReaderData data = new ReaderData();

            data.id = reader.getId();
            data.fullName = reader.getFullName();
            data.phone = reader.getPhone();
            data.type = reader.getReaderType();

            if (reader instanceof StudentReader) {
                data.studentId =
                        ((StudentReader) reader).getStudentId();
            }

            if (reader instanceof LecturerReader) {
                data.department =
                        ((LecturerReader) reader).getDepartment();
            }

            snapshot.readers.add(data);
        }

        // Luu phieu muon
        for (BorrowRecord record : manager.getBorrowRecords()) {

            BorrowData data = new BorrowData();

            data.id = record.getId();
            data.readerId = record.getReader().getId();
            data.documentId = record.getDocument().getId();

            data.borrowDate = record.getBorrowDate().toString();
            data.dueDate = record.getDueDate().toString();

            if (record.getReturnDate() != null) {
                data.returnDate =
                        record.getReturnDate().toString();
            }

            snapshot.borrowRecords.add(data);
        }

        try (FileWriter writer = new FileWriter(FILE_NAME)) {

            gson.toJson(snapshot, writer);

            System.out.println("Luu du lieu thanh cong!");

        } catch (IOException e) {

            System.out.println(
                    "Loi khi luu du lieu: " + e.getMessage()
            );
        }
    }

    // DOC DU LIEU
    public static LibraryManager load(Class<LibraryManager> type) {

        LibrarySnapshot snapshot;

        try (FileReader reader = new FileReader(FILE_NAME)) {

            snapshot = gson.fromJson(reader, LibrarySnapshot.class);

        } catch (IOException e) {

            System.out.println(
                    "Chua co file du lieu, se tao du lieu moi."
            );

            return null;

        } catch (Exception e) {

            System.out.println(
                    "Loi doc du lieu: " + e.getMessage()
            );

            return null;
        }

        if (snapshot == null) {
            return null;
        }

        LibraryManager manager = new LibraryManager();

        // Khoi phuc tai lieu
        if (snapshot.documents != null) {

            for (DocumentData data : snapshot.documents) {

                Document document = null;

                switch (data.type) {

                    case "Sách":
                        document = new Book(
                                data.id,
                                data.title,
                                data.author,
                                data.quantity,
                                data.publisher
                        );
                        break;

                    case "Tạp chí":
                        document = new Magazine(
                                data.id,
                                data.title,
                                data.author,
                                data.quantity,
                                data.issueNumber
                        );
                        break;

                    case "Luận văn":
                        document = new Thesis(
                                data.id,
                                data.title,
                                data.author,
                                data.quantity,
                                data.university
                        );
                        break;
                }

                if (document != null) {
                    manager.add(document);
                }
            }
        }

        // Khoi phuc doc gia
        if (snapshot.readers != null) {

            for (ReaderData data : snapshot.readers) {

                Reader reader = null;

                if ("Sinh viên".equals(data.type)) {

                    reader = new StudentReader(
                            data.id,
                            data.fullName,
                            data.phone,
                            data.studentId
                    );

                } else if ("Giảng viên".equals(data.type)) {

                    reader = new LecturerReader(
                            data.id,
                            data.fullName,
                            data.phone,
                            data.department
                    );
                }

                if (reader != null) {
                    manager.addReader(reader);
                }
            }
        }

        // Khoi phuc phieu muon
        if (snapshot.borrowRecords != null) {

            for (BorrowData data : snapshot.borrowRecords) {

                Reader reader =
                        manager.findReaderById(data.readerId);

                Document document =
                        manager.findById(data.documentId);

                if (reader == null || document == null) {
                    continue;
                }

                BorrowRecord record = new BorrowRecord(
                        data.id,
                        reader,
                        document,
                        LocalDate.parse(data.borrowDate),
                        LocalDate.parse(data.dueDate)
                );

                if (data.returnDate != null) {
                    record.setReturnDate(
                            LocalDate.parse(data.returnDate)
                    );
                }

                manager.addBorrowRecord(record);
            }
        }

        System.out.println("Khoi phuc du lieu thanh cong!");

        return manager;
    }
}
