
package view;

import controller.LibraryManager;
import model.Book;
import data.LibraryData;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {

        // Doc du lieu da luu
        LibraryManager loadedManager =
                LibraryData.load(LibraryManager.class);

        // Neu chua co du lieu thi tao quan ly moi
        final LibraryManager manager;

        if (loadedManager == null) {
            manager = new LibraryManager();

            manager.add(new Book(
                    "B001",
                    "Lap trinh Java",
                    "Nguyen Van A",
                    10,
                    "NXB Giao Duc"
            ));
        } else {
            manager = loadedManager;
        }

        SwingUtilities.invokeLater(() -> {

            LibraryView view = new LibraryView(manager);

            view.setDefaultCloseOperation(
                    javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
            );

            view.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LibraryData.save(manager);
                    view.dispose();
                }
            });

            view.setVisible(true);
        });
    }
}
