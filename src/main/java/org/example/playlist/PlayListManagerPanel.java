package org.example.playlist;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class PlayListManagerPanel extends JPanel {

    private String selectedPlaylistItem = null; // Seçilen öğeyi saklayan değişken

    private String playlistFilePath;  // Örneğin "playlist1.txt"
    private File lastChosenDirectory = null;

    // Eğer dosya varsa ana arayüzde kullanılacak bileşenler:
    private JButton addFileButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton deleteButton;
    private JButton copyFullPathButton;
    private JButton openInExplorerButton;
    private JButton savePlaylistButton;

    // Liste (playlist) bileşenleri
    private DefaultListModel<String> listModel;
    private JList<String> playlistList;

    // Eğer dosya yoksa görünecek buton
    private JButton createFileButton;

    public PlayListManagerPanel(String playlistFilePath) {
        this.playlistFilePath = playlistFilePath;
        initUI();
    }

    public String getPlaylistFilePath() {
        return playlistFilePath;
    }

    public void setPlaylistFilePath(String playlistFilePath) {
        this.playlistFilePath = playlistFilePath;
    }

    /**
     * Dosya var mı yok mu kontrol edip uygun arayüzü oluşturur.
     */
    private void initUI() {
        setLayout(new BorderLayout());
        File playlistFile = new File(playlistFilePath);

        if (!playlistFile.exists()) {
            // Dosya yoksa: sadece buton
            removeAll();
            createFileButton = new JButton(playlistFilePath + " bulunamadı, oluşturayım mı?");
            createFileButton.addActionListener(e -> {
                try {
                    playlistFile.createNewFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    return;
                }
                removeAll();
                buildMainUI(); // Dosya oluşturulduktan sonra ana arayüzü kur
                revalidate();
                repaint();
            });
            add(createFileButton, BorderLayout.CENTER);

        } else {
            // Dosya varsa, doğrudan arayüzü kur
            buildMainUI();
        }
    }

    /**
     * Ana (playlist) arayüzü oluşturur.
     */
    private void buildMainUI() {
        removeAll();
        setLayout(new BorderLayout());

        // Üst tarafta kontrol butonları
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addFileButton = new JButton("Add Audio File");
        moveUpButton = new JButton("Move Up");
        moveDownButton = new JButton("Move Down");
        deleteButton = new JButton("Delete");
        copyFullPathButton = new JButton("Copy full path");
        openInExplorerButton = new JButton("Open in File Explorer");
        savePlaylistButton = new JButton("Save Playlist");

        topPanel.add(addFileButton);
        topPanel.add(moveUpButton);
        topPanel.add(moveDownButton);
        topPanel.add(deleteButton);
        topPanel.add(copyFullPathButton);
        topPanel.add(openInExplorerButton);
        topPanel.add(savePlaylistButton);

        add(topPanel, BorderLayout.NORTH);

        // Liste ve model
        listModel = new DefaultListModel<>();
        playlistList = new JList<>(listModel);
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Playlist dosyasını yükle
        loadPlaylistFromFile();

        // JList'i ScrollPane içine alalım
        JScrollPane scrollPane = new JScrollPane(playlistList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // ActionListenerlar
        addFileButton.addActionListener(this::onAddFile);
        moveUpButton.addActionListener(e -> onMoveUp());
        moveDownButton.addActionListener(e -> onMoveDown());
        deleteButton.addActionListener(e -> onDelete());
        copyFullPathButton.addActionListener(e -> onCopyFullPath());
        openInExplorerButton.addActionListener(e -> onOpenInExplorer());
        savePlaylistButton.addActionListener(e -> onSavePlaylist());

        // Çift tıklama olayını yakala
        playlistList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Çift tıklama kontrolü
                    selectedPlaylistItem = playlistList.getSelectedValue(); // Seçilen öğeyi al

                    onSavePlaylist();
                    SwingUtilities.getWindowAncestor(PlayListManagerPanel.this).dispose(); // JDialog'u kapat
                }
            }
        });





    }

    /**
     * playlistFilePath dosyasını okuyup listModel'e ekler.
     */
    private void loadPlaylistFromFile() {
        File playlistFile = new File(playlistFilePath);
        if (!playlistFile.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(playlistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    listModel.addElement(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * "Add Audio File" butonuna tıklanınca çalışır.
     */
    private void onAddFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        // En son kullanılan dizini hatırlıyorsak, oradan açalım
        if (lastChosenDirectory != null && lastChosenDirectory.exists()) {
            fileChooser.setCurrentDirectory(lastChosenDirectory);
        }

        // Klasör veya mp3/m4a dosyalarını gösterecek biçimde filtre
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".mp3") || name.endsWith(".m4a");
            }

            @Override
            public String getDescription() {
                return "MP3 ve M4A dosyaları veya klasörler";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            if (selected.isFile()) {
                // Tek dosya ekle
                listModel.addElement(selected.getAbsolutePath());
                lastChosenDirectory = selected.getParentFile();
            } else if (selected.isDirectory()) {
                // Klasördeki tüm mp3 ve m4a dosyalarını ekle
                addAllAudioFilesInFolder(selected);
                lastChosenDirectory = selected;
            }
        }
    }

    /**
     * Verilen klasör içerisindeki tüm (tek seviye) mp3/m4a dosyalarını listeye ekler.
     */
    private void addAllAudioFilesInFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".mp3") || name.endsWith(".m4a")) {
                    listModel.addElement(f.getAbsolutePath());
                }
            }
            // Eğer alt klasörleri de taramak isterseniz,
            // f.isDirectory() ise recursive çağrı yapabilirsiniz.
        }
    }

    /**
     * "Move Up" butonuna tıklanınca çalışır.
     */
    private void onMoveUp() {
        int index = playlistList.getSelectedIndex();
        if (index > 0) {
            // Seçili elemanı bir üst elemanla yer değiştir
            String currentItem = listModel.getElementAt(index);
            String aboveItem = listModel.getElementAt(index - 1);

            listModel.setElementAt(aboveItem, index);
            listModel.setElementAt(currentItem, index - 1);

            playlistList.setSelectedIndex(index - 1);
        }
    }

    /**
     * "Move Down" butonuna tıklanınca çalışır.
     */
    private void onMoveDown() {
        int index = playlistList.getSelectedIndex();
        if (index >= 0 && index < listModel.size() - 1) {
            // Seçili elemanı bir alt elemanla yer değiştir
            String currentItem = listModel.getElementAt(index);
            String belowItem = listModel.getElementAt(index + 1);

            listModel.setElementAt(belowItem, index);
            listModel.setElementAt(currentItem, index + 1);

            playlistList.setSelectedIndex(index + 1);
        }
    }

    /**
     * "Delete" butonuna tıklanınca çalışır.
     */
    private void onDelete() {
        int index = playlistList.getSelectedIndex();
        if (index >= 0) {
            listModel.remove(index);
        }
    }

    /**
     * "Copy full path" butonuna tıklanınca çalışır.
     */
    private void onCopyFullPath() {
        int index = playlistList.getSelectedIndex();
        if (index >= 0) {
            String fullPath = listModel.getElementAt(index);
            StringSelection selection = new StringSelection(fullPath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }

    /**
     * "Open in File Explorer" butonuna tıklanınca çalışır (Windows örneği).
     */
    private void onOpenInExplorer() {
        int index = playlistList.getSelectedIndex();
        if (index >= 0) {
            String fullPath = listModel.getElementAt(index);
            File file = new File(fullPath);
            if (file.exists()) {
                try {
                    // Windows için
                    Runtime.getRuntime().exec("explorer /select,\"" + file.getAbsolutePath() + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * "Save Playlist" butonuna tıklanınca çalışır.
     * Listedeki tüm öğeleri playlistFilePath dosyasına yazar (overwrite).
     */
    private void onSavePlaylist() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(playlistFilePath, false))) {
            for (int i = 0; i < listModel.size(); i++) {
                pw.println(listModel.getElementAt(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test amaçlı main metodu.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlayListManagerPanel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // "playlist1.txt" isimli dosyanın olup olmadığını kontrol edeceğiz
            PlayListManagerPanel panel = new PlayListManagerPanel("playlist1.txt");

            frame.getContentPane().add(panel);
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


    // Seçili playlist değerini döndüren metod
    public String getSelectedPlaylistItem() {
        return selectedPlaylistItem;
    }

}
