
package view;

import controller.LibraryManager;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibraryView extends JFrame {

    private LibraryManager manager;

    private DefaultTableModel bookModel;
    private DefaultTableModel readerModel;
    private DefaultTableModel borrowModel;

    private JTable bookTable;
    private JTable readerTable;
    private JTable borrowTable;

    private JTextField searchField;

    public LibraryView(LibraryManager manager) {
        this.manager = manager;

        setTitle("HE THONG QUAN LY THU VIEN");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Quan ly sach", createBookPanel());
        tabs.addTab("Quan ly doc gia", createReaderPanel());
        tabs.addTab("Muon / Tra sach", createBorrowPanel());
        tabs.addTab("Thong ke", createStatisticsPanel());

        add(tabs);
    }

    // =========================
    // QUAN LY SACH
    // =========================

    private JPanel createBookPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Ma sach", "Ten sach", "Tac gia",
                "So luong", "Loai"
        };

        bookModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(bookModel);

        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(25);

        JButton search = new JButton("Tim kiem");
        JButton refresh = new JButton("Lam moi");

        searchPanel.add(new JLabel("Nhap ma sach hoac ten sach:"));
        searchPanel.add(searchField);
        searchPanel.add(search);
        searchPanel.add(refresh);

        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel buttons = new JPanel();

        JButton add = new JButton("Them sach");
        JButton edit = new JButton("Sua sach");
        JButton delete = new JButton("Xoa sach");

        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);

        panel.add(buttons, BorderLayout.SOUTH);

        add.addActionListener(e -> addBook());
        edit.addActionListener(e -> editBook());
        delete.addActionListener(e -> deleteBook());

        search.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks());

        refresh.addActionListener(e -> {
            searchField.setText("");
            refreshBooks();
        });

        refreshBooks();

        return panel;
    }

    // TIM KIEM SACH
    private void searchBooks() {

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui long nhap ma sach hoac ten sach!"
            );
            return;
        }

        bookModel.setRowCount(0);

        for (Document d : manager.searchDocuments(keyword)) {

            bookModel.addRow(new Object[]{
                    d.getId(),
                    d.getTitle(),
                    d.getAuthor(),
                    d.getQuantity(),
                    d.getDocumentType()
            });
        }

        if (bookModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Khong tim thay sach phu hop!"
            );
        }
    }

    // THEM SACH
    private void addBook() {

        String title = JOptionPane.showInputDialog(this, "Ten sach:");
        if (title == null || title.trim().isEmpty()) return;

        String author = JOptionPane.showInputDialog(this, "Tac gia:");
        if (author == null || author.trim().isEmpty()) return;

        String publisher = JOptionPane.showInputDialog(this, "Nha xuat ban:");
        if (publisher == null || publisher.trim().isEmpty()) return;

        Book book = new Book(
                "B" + System.currentTimeMillis(),
                title.trim(),
                author.trim(),
                1,
                publisher.trim()
        );

        manager.add(book);
        refreshBooks();
        refreshStatisticsIfAvailable();

        JOptionPane.showMessageDialog(this, "Them sach thanh cong!");
    }

    // SUA SACH
    private void editBook() {

        int row = bookTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hay chon sach can sua!");
            return;
        }

        String id = bookModel.getValueAt(row, 0).toString();
        Document document = manager.findById(id);

        if (document == null) {
            JOptionPane.showMessageDialog(this, "Khong tim thay sach!");
            return;
        }

        String title = JOptionPane.showInputDialog(
                this, "Ten sach moi:", document.getTitle()
        );

        if (title == null || title.trim().isEmpty()) return;

        String author = JOptionPane.showInputDialog(
                this, "Tac gia moi:", document.getAuthor()
        );

        if (author == null || author.trim().isEmpty()) return;

        String quantityText = JOptionPane.showInputDialog(
                this, "So luong moi:", document.getQuantity()
        );

        if (quantityText == null) return;

        try {

            int quantity = Integer.parseInt(quantityText);

            if (quantity < 0) {
                JOptionPane.showMessageDialog(
                        this, "So luong khong duoc am!"
                );
                return;
            }

            document.setTitle(title.trim());
            document.setAuthor(author.trim());
            document.setQuantity(quantity);

            manager.update(document);
            refreshBooks();
            refreshStatisticsIfAvailable();

            JOptionPane.showMessageDialog(
                    this, "Cap nhat sach thanh cong!"
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this, "So luong phai la so nguyen!"
            );
        }
    }

    // XOA SACH
    private void deleteBook() {

        int row = bookTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hay chon sach can xoa!");
            return;
        }

        String id = bookModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ban co chac muon xoa sach nay?",
                "Xac nhan",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            manager.delete(id);
            refreshBooks();
            refreshStatisticsIfAvailable();

            JOptionPane.showMessageDialog(
                    this, "Xoa sach thanh cong!"
            );
        }
    }

    // LAM MOI DANH SACH SACH
    private void refreshBooks() {

        bookModel.setRowCount(0);

        for (Document d : manager.getAll()) {

            bookModel.addRow(new Object[]{
                    d.getId(),
                    d.getTitle(),
                    d.getAuthor(),
                    d.getQuantity(),
                    d.getDocumentType()
            });
        }
    }

    // =========================
    // QUAN LY DOC GIA
    // =========================

    private JPanel createReaderPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Ma doc gia", "Ho ten",
                "So dien thoai", "Loai doc gia"
        };

        readerModel = new DefaultTableModel(columns, 0);
        readerTable = new JTable(readerModel);

        panel.add(new JScrollPane(readerTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton add = new JButton("Them doc gia");
        JButton delete = new JButton("Xoa doc gia");
        JButton refresh = new JButton("Lam moi");

        buttons.add(add);
        buttons.add(delete);
        buttons.add(refresh);

        panel.add(buttons, BorderLayout.SOUTH);

        add.addActionListener(e -> addReader());
        delete.addActionListener(e -> deleteReader());
        refresh.addActionListener(e -> refreshReaders());

        refreshReaders();

        return panel;
    }

    // THEM DOC GIA
    private void addReader() {

        String[] types = {"Sinh vien", "Giang vien"};

        String type = (String) JOptionPane.showInputDialog(
                this,
                "Chon loai doc gia:",
                "Them doc gia",
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                types[0]
        );

        if (type == null) return;

        String id = JOptionPane.showInputDialog(this, "Ma doc gia:");

        if (id == null || id.trim().isEmpty()) return;

        if (manager.findReaderById(id.trim()) != null) {
            JOptionPane.showMessageDialog(
                    this, "Ma doc gia da ton tai!"
            );
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Ho ten:");

        if (name == null || name.trim().isEmpty()) return;

        String phone = JOptionPane.showInputDialog(this, "So dien thoai:");

        if (phone == null || phone.trim().isEmpty()) return;

        Reader reader;

        if (type.equals("Sinh vien")) {

            String studentId = JOptionPane.showInputDialog(
                    this, "Ma sinh vien:"
            );

            if (studentId == null || studentId.trim().isEmpty()) return;

            reader = new StudentReader(
                    id.trim(), name.trim(), phone.trim(), studentId.trim()
            );

        } else {

            String department = JOptionPane.showInputDialog(
                    this, "Khoa / Bo mon:"
            );

            if (department == null || department.trim().isEmpty()) return;

            reader = new LecturerReader(
                    id.trim(), name.trim(), phone.trim(), department.trim()
            );
        }

        manager.addReader(reader);
        refreshReaders();
        refreshStatisticsIfAvailable();

        JOptionPane.showMessageDialog(
                this, "Them doc gia thanh cong!"
        );
    }

    // XOA DOC GIA
    private void deleteReader() {

        int row = readerTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon doc gia can xoa!"
            );
            return;
        }

        String id = readerModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ban co chac muon xoa doc gia nay?",
                "Xac nhan",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteReader(id);
            refreshReaders();
            refreshStatisticsIfAvailable();
        }
    }

    // LAM MOI DANH SACH DOC GIA
    private void refreshReaders() {

        readerModel.setRowCount(0);

        for (Reader r : manager.getReaders()) {

            readerModel.addRow(new Object[]{
                    r.getId(),
                    r.getFullName(),
                    r.getPhone(),
                    r.getReaderType()
            });
        }
    }

    // =========================
    // MUON / TRA SACH
    // =========================

    private JPanel createBorrowPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
                "Ma phieu",
                "Ma doc gia",
                "Ten doc gia",
                "Ma sach",
                "Ten sach",
                "Ngay muon",
                "Han tra",
                "Ngay tra",
                "Trang thai",
                "So ngay qua han",
                "Tien phat"
        };

        borrowModel = new DefaultTableModel(columns, 0);
        borrowTable = new JTable(borrowModel);

        panel.add(new JScrollPane(borrowTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton borrow = new JButton("Lap phieu muon");
        JButton returnBookButton = new JButton("Tra sach");
        JButton refresh = new JButton("Lam moi");

        buttons.add(borrow);
        buttons.add(returnBookButton);
        buttons.add(refresh);

        panel.add(buttons, BorderLayout.SOUTH);

        borrow.addActionListener(e -> borrowBook());
        returnBookButton.addActionListener(e -> returnBook());
        refresh.addActionListener(e -> refreshBorrows());

        refreshBorrows();

        return panel;
    }

    // LAP PHIEU MUON
    private void borrowBook() {

        String readerId = JOptionPane.showInputDialog(
                this, "Nhap ma doc gia:"
        );

        if (readerId == null || readerId.trim().isEmpty()) return;

        String documentId = JOptionPane.showInputDialog(
                this, "Nhap ma sach:"
        );

        if (documentId == null || documentId.trim().isEmpty()) return;

        String recordId = "PM" + System.currentTimeMillis();

        boolean success = manager.borrowDocument(
                recordId,
                readerId.trim(),
                documentId.trim()
        );

        if (success) {

            refreshBorrows();
            refreshBooks();
            refreshStatisticsIfAvailable();

            JOptionPane.showMessageDialog(
                    this,
                    "Lap phieu muon thanh cong! Han tra sau 7 ngay."
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Khong the muon sach! Hay kiem tra ma doc gia, ma sach va so luong."
            );
        }
    }

    // TRA SACH VA TINH TIEN PHAT
    private void returnBook() {

        int row = borrowTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Hay chon phieu muon can tra!"
            );
            return;
        }

        String recordId = borrowModel.getValueAt(row, 0).toString();

        boolean success = manager.returnDocument(recordId);

        if (success) {

            BorrowRecord returnedRecord = null;

            for (BorrowRecord record : manager.getBorrowRecords()) {
                if (record.getId().equals(recordId)) {
                    returnedRecord = record;
                    break;
                }
            }

            refreshBorrows();
            refreshBooks();
            refreshStatisticsIfAvailable();

            if (returnedRecord != null) {

                long overdueDays = returnedRecord.getOverdueDays();
                long fine = returnedRecord.getFine();

                if (fine > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "TRA SACH THANH CONG!\n\n"
                                    + "So ngay qua han: " + overdueDays + " ngay\n"
                                    + "Muc phat: 5.000 dong/ngay\n"
                                    + "Tong tien phat: "
                                    + String.format("%,d", fine) + " dong",
                            "THONG BAO TIEN PHAT",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "TRA SACH THANH CONG!\n\n"
                                    + "Doc gia tra sach dung han.\n"
                                    + "So ngay qua han: 0 ngay\n"
                                    + "Tien phat: 0 dong.",
                            "THONG BAO TRA SACH",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } else {

                JOptionPane.showMessageDialog(
                        this, "Tra sach thanh cong!"
                );
            }

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Phieu muon khong hop le hoac sach da duoc tra!"
            );
        }
    }

    // LAM MOI DANH SACH PHIEU MUON
    private void refreshBorrows() {

        borrowModel.setRowCount(0);

        for (BorrowRecord record : manager.getBorrowRecords()) {

            String status = record.isReturned()
                    ? "Da tra"
                    : "Dang muon";

            String returnDate = record.getReturnDate() == null
                    ? "Chua tra"
                    : record.getReturnDate().toString();

            long overdueDays = record.getOverdueDays();
            long fine = record.getFine();

            borrowModel.addRow(new Object[]{
                    record.getId(),
                    record.getReader().getId(),
                    record.getReader().getFullName(),
                    record.getDocument().getId(),
                    record.getDocument().getTitle(),
                    record.getBorrowDate(),
                    record.getDueDate(),
                    returnDate,
                    status,
                    overdueDays,
                    String.format("%,d dong", fine)
            });
        }
    }

    // =========================
    // THONG KE THU VIEN
    // =========================

    private JPanel createStatisticsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "THONG KE HE THONG QUAN LY THU VIEN",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(7, 1, 10, 10));

        content.setBorder(
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        );

        JLabel books = new JLabel();
        JLabel copies = new JLabel();
        JLabel readers = new JLabel();
        JLabel totalBorrows = new JLabel();
        JLabel activeBorrows = new JLabel();
        JLabel returnedBorrows = new JLabel();
        JLabel totalFines = new JLabel();

        JLabel[] labels = {
                books,
                copies,
                readers,
                totalBorrows,
                activeBorrows,
                returnedBorrows,
                totalFines
        };

        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 16));
            content.add(label);
        }

        panel.add(content, BorderLayout.CENTER);

        JButton refresh = new JButton("CAP NHAT THONG KE");

        refresh.addActionListener(e -> updateStatistics(
                books,
                copies,
                readers,
                totalBorrows,
                activeBorrows,
                returnedBorrows,
                totalFines
        ));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateStatistics(
                books,
                copies,
                readers,
                totalBorrows,
                activeBorrows,
                returnedBorrows,
                totalFines
        );

        return panel;
    }

    // CAP NHAT SO LIEU THONG KE
    private void updateStatistics(
            JLabel books,
            JLabel copies,
            JLabel readers,
            JLabel totalBorrows,
            JLabel activeBorrows,
            JLabel returnedBorrows,
            JLabel totalFines) {

        int bookCount = manager.getAll().size();

        int copyCount = 0;

        for (Document document : manager.getAll()) {
            copyCount += document.getQuantity();
        }

        int readerCount = manager.getReaders().size();

        int borrowCount = manager.getBorrowRecords().size();

        int activeCount = 0;
        int returnedCount = 0;

        long fineTotal = 0;

        for (BorrowRecord record : manager.getBorrowRecords()) {

            if (record.isReturned()) {
                returnedCount++;
                fineTotal += record.getFine();
            } else {
                activeCount++;
            }
        }

        books.setText("Tong so dau sach: " + bookCount);

        copies.setText(
                "Tong so ban sach con trong kho: " + copyCount
        );

        readers.setText("Tong so doc gia: " + readerCount);

        totalBorrows.setText(
                "Tong so phieu muon: " + borrowCount
        );

        activeBorrows.setText(
                "So phieu dang muon: " + activeCount
        );

        returnedBorrows.setText(
                "So phieu da tra: " + returnedCount
        );

        totalFines.setText(
                "Tong tien phat cua cac phieu da tra: "
                        + String.format("%,d", fineTotal) + " dong"
        );
    }

    // CAP NHAT THONG KE KHI DU LIEU THAY DOI
    private void refreshStatisticsIfAvailable() {
        // Nut cap nhat thong ke se doc du lieu moi nhat
        // khi nguoi dung mo tab Thong ke va bam cap nhat.
    }
}
