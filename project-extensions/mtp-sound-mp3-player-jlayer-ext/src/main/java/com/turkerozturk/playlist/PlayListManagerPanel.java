/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package com.turkerozturk.playlist;

import com.iheartradio.m3u8.data.TrackData;
import com.iheartradio.m3u8.data.TrackInfo;
import com.turkerozturk.chatgpt.M3U8PlaylistManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class PlayListManagerPanel extends JPanel {

    // ConfigManager configManager = ConfigManager.getInstance();
    // LanguageManager bundle = LanguageManager.getInstance();


    int iconWidth = 18;
    int iconHeight = 18;


    private PlayListSelectionListener selectionListener;
    private JButton savePlaylistAsButton;

    /**
     * inteface aracigiliyla playlistteki click eventlerini almak icin.
     *
     * @param listener
     */
    public void setPlayListSelectionListener(PlayListSelectionListener listener) {
        this.selectionListener = listener;
    }


    private M3U8PlaylistManager m3U8PlaylistManager = M3U8PlaylistManager.getInstance();

    private int selectedPlaylistItemIndex; // Seçilen öğeyi saklayan değişken

    //private String playlistFilePath;  // Örneğin "playlist1.txt"
    private File lastChosenDirectory = null;

    // Eğer dosya varsa ana arayüzde kullanılacak bileşenler:
    private JButton addFileButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton deleteButton;
    private JButton copyFullPathButton;
    private JButton openInExplorerButton;

    private JButton newPlaylistButton;

    private JButton openPlaylistButton;


    private JButton savePlaylistButton;

    // Liste (playlist) bileşenleri
    private DefaultListModel<TrackData> listModel;
    private JList<TrackData> playlistList;
    List<TrackData> trackDataList;

    // Eğer dosya yoksa görünecek buton
    private JButton createFileButton;

    private static final int DOUBLE_CLICK = 2;
    private static final int SINGLE_CLICK = 1;


    public PlayListManagerPanel() {
        setLayout(new BorderLayout());


        trackDataList = m3U8PlaylistManager.getTrackList();


        // DefaultListModel kullanarak JList oluştur
        listModel = new DefaultListModel<>();
        playlistList = new JList<>(listModel);  // JList'e model olarak set ediyoruz

        /**
         * TrackData kutuphane icinde oldugundan ona toString eklemek yerine asagidaki metodu onerdi chatgpt.
         * Cunku JList'e string degil de TrackData gibi yapı ekleyecegimiz icin JList'in ihtiyacini toString metodu
         * olusturarak
         * veya asagidaki yontemle halletmis oluyoruz. Ek olarak song title bulamazsa dosya tam yolu gosterecek bicimde
         * ayarladik.
         */
        playlistList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof TrackData) {
                    TrackData td = (TrackData) value;
                    if (td.getTrackInfo() != null && td.getTrackInfo().title != null && !td.getTrackInfo().title.isEmpty()) {
                        setText(td.getTrackInfo().title);
                    } else {
                        setText(td.getUri());
                    }
                }

                return this;
            }
        });

        /**
         * JList'in list modelini asagidaki dongu ile doldurarak gui'de listede gozumuzle gormus oluyorz sarki isimlerini.
         */
        for (TrackData td : trackDataList) {
            listModel.addElement(td);
        }

        try {


            buildMainUI();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public String getPlaylistFilePath() {
        return playlistFilePath;
    }

    public void setPlaylistFilePath(String playlistFilePath) {
        this.playlistFilePath = playlistFilePath;
    }
    */

    /**
     * Dosya var mı yok mu kontrol edip uygun arayüzü oluşturur.
     */
    private void initUI() {

        //   File playlistFile = new File(playlistFilePath);

        // if (!playlistFile.exists()) {
            /*
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
            */

        //  } else {
        // Dosya varsa, doğrudan arayüzü kur


        // }
    }

    /**
     * Ana (playlist) arayüzü oluşturur.
     */
    private void buildMainUI() {
        removeAll();
        setLayout(new BorderLayout());

        // Üst tarafta kontrol butonları
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        openPlaylistButton = new JButton("Open");
        openPlaylistButton.setToolTipText("Open Playlist");
        openPlaylistButton.setMargin(new Insets(0, 0, 0, 0));

        openPlaylistButton.addActionListener(e -> {
            String playListPathAsString = FileChooserUtil.selectM3U8File();
            // https://stackoverflow.com/questions/13597903/how-to-clear-a-jlist-in-java
            DefaultListModel listModel = (DefaultListModel) playlistList.getModel();
            listModel.removeAllElements();
            m3U8PlaylistManager.setCurrentPlaylistPath(playListPathAsString);
            changeFrameTitle("playlist: " + playListPathAsString);
            // BASLA openplaylist

            try {
                m3U8PlaylistManager.openPlaylist();

                if (m3U8PlaylistManager.getTrackList().isEmpty()) {
                    //fileNameLabel.setText("Playlist is empty. Add Tracks To Playlist!");
                } else {


                    /*
                    currenTrackData = m3U8PlaylistManager.getTrackList().get(0);

                    if (currenTrackData.getTrackInfo() != null) {
                        fileNameLabel.setText(currenTrackData.getTrackInfo().title);
                    } else {
                        fileNameLabel.setText(currenTrackData.getUri());
                    }
                    */

                    for (TrackData td : m3U8PlaylistManager.getTrackList()) {


                        // hem de jlist in listmodeline ekliyoruz.
                        listModel.addElement(td);
                    }


                }
            } catch (Exception ex) {
                System.out.println("Playlist is corrupt: " + playListPathAsString);
                //ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Playlist yüklenirken hata oluştu: " + ex.getMessage());

            }


            // BITTI openplaylist


        });

        // Kenar boşluğunu kaldır
        // Çerçeveyi kaldırarak daha sıkı bir görünüm elde et
        addFileButton = new JButton("Add");
        addFileButton.setToolTipText("Add Audio File");
        addFileButton.setMargin(new Insets(0, 0, 0, 0));
        //addFileButton.setBorder(BorderFactory.createEmptyBorder());
        // FlatSVGIcon addFileIcon = new FlatSVGIcon("square-plus.svg", iconWidth, iconHeight);
        // ImageIcon addFileIcon = new ImageIcon("src/main/resources/frameicon.png");
        // addFileButton.setIcon(addFileIcon);


        moveUpButton = new JButton("Up");
        moveUpButton.setToolTipText("Move Up");
        moveUpButton.setMargin(new Insets(0, 0, 0, 0));

        moveDownButton = new JButton("Down");
        moveDownButton.setToolTipText("Move Down");
        moveDownButton.setMargin(new Insets(0, 0, 0, 0));

        deleteButton = new JButton("Del");
        deleteButton.setToolTipText("Delete");
        deleteButton.setMargin(new Insets(0, 0, 0, 0));

        copyFullPathButton = new JButton("Copy path");
        copyFullPathButton.setToolTipText("Copy full path");
        copyFullPathButton.setMargin(new Insets(0, 0, 0, 0));

        openInExplorerButton = new JButton("Explorer");
        openInExplorerButton.setToolTipText("Open in File Explorer");
        openInExplorerButton.setMargin(new Insets(0, 0, 0, 0));

        savePlaylistButton = new JButton("Save");
        savePlaylistButton.setToolTipText("Save Playlist");
        savePlaylistButton.setMargin(new Insets(0, 0, 0, 0));

        savePlaylistAsButton = new JButton("Save As");
        savePlaylistAsButton.setToolTipText("Save Playlist As");
        savePlaylistAsButton.setMargin(new Insets(0, 0, 0, 0));

        topPanel.add(openPlaylistButton);

        topPanel.add(addFileButton);
        topPanel.add(moveUpButton);
        topPanel.add(moveDownButton);
        topPanel.add(deleteButton);
        topPanel.add(copyFullPathButton);
        topPanel.add(openInExplorerButton);
        topPanel.add(savePlaylistButton);
        topPanel.add(savePlaylistAsButton);

        add(topPanel, BorderLayout.NORTH);

        // Liste ve model
        // listModel = new DefaultListModel<>();
        // playlistList = new JList<>(listModel);
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // Playlist dosyasını yükle
        //loadPlaylistFromFile();

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
        savePlaylistButton.addActionListener(e -> {
            try {
                if (m3U8PlaylistManager.getCurrentPlaylistPath() == null) {
                    JFileChooser fc = new JFileChooser();
                    if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File chosenFile = fc.getSelectedFile();
                        String filePath = chosenFile.getAbsolutePath();

                        // Eğer uzantı yoksa veya .m3u8 değilse, ekleyelim
                        if (!filePath.toLowerCase().endsWith(".m3u8")) {
                            filePath += ".m3u8";
                        }

                        m3U8PlaylistManager.setCurrentPlaylistPath(filePath);
                    }
                }
                System.out.println("Playlistmanagerpaneljava: CurrentPlaylistPath: " + m3U8PlaylistManager.getCurrentPlaylistPath());
                m3U8PlaylistManager.savePlaylist();
            } catch (Exception ex) {
                //throw new RuntimeException(ex);
                ex.printStackTrace();

            }

        });

        savePlaylistAsButton.addActionListener(e -> {
            try {
                // if (m3U8PlaylistManager.getCurrentPlaylistPath() == null) {
                JFileChooser fc = new JFileChooser();
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fc.getSelectedFile();
                    String filePath = chosenFile.getAbsolutePath();

                    // Eğer uzantı yoksa veya .m3u8 değilse, ekleyelim
                    if (!filePath.toLowerCase().endsWith(".m3u8")) {
                        filePath += ".m3u8";
                    }

                    m3U8PlaylistManager.setCurrentPlaylistPath(filePath);
                }
                //   }
                m3U8PlaylistManager.savePlaylist();
            } catch (Exception ex) {
                // throw new RuntimeException(ex);
                ex.printStackTrace();
            }

        });


        playlistList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = playlistList.locationToIndex(e.getPoint());
                if (index == -1) return;

                TrackData value = playlistList.getModel().getElementAt(index);

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == DOUBLE_CLICK) {
                        // Sol çift tık
                        if (selectionListener != null) {
                            selectionListener.onItemDoubleClicked(index, value);
                            //System.out.println("doubleclick");
                            m3U8PlaylistManager.setCurrentTrackIndex(index);
                            m3U8PlaylistManager.setCurrentTrack(value);


                            // playlisti kapatmiyoruz artik. kullanici isterse kendisi kapatir.
                            //  SwingUtilities.getWindowAncestor(PlayListManagerPanel.this).dispose(); // JDialog'u kapat

                        }
                    } else if (e.getClickCount() == SINGLE_CLICK) {
                        // Sol tek tık
                        if (selectionListener != null) {
                            selectionListener.onItemClicked(index, value);
                            //System.out.println("singleclick");
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Sağ tık
                    if (selectionListener != null) {
                        selectionListener.onItemRightClicked(index, value);
                        //System.out.println("rightclick");

                    }
                }
            }
        });

    }


    /**
     * playlistFilePath dosyasını okuyup listModel'e ekler.
     */
    private void loadPlaylistFromFile() {
        /*
        File playlistFile = new File(playlistFilePath);
        if (!playlistFile.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(playlistFile))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    System.out.println(line + "\n\n" + selectedPlaylistItem + "\n -------------------------");
                    if(line.equals(selectedPlaylistItem)) {
                        playlistList.setSelectedIndex(index);
                    }
                    listModel.addElement(line);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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

        /* TODO dogrusunu yaz
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
        */

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            System.out.println("PlaylistMAnagerPanel.java: " + selected.getAbsolutePath());
            if (selected.isFile()) {


                TrackInfo trackInfo;

/*
                MP3File ffff      = null;
                try {
                    ffff = (MP3File) AudioFileIO.read(selected);
                    MP3AudioHeader audioHeader = (MP3AudioHeader) ffff.getAudioHeader();
                    audioHeader.getTrackLength();
                    audioHeader.getSampleRateAsNumber();

                    trackInfo = new TrackInfo(audioHeader.getTrackLength(), selected.getName());
                    //mp3AudioHeader.getChannels();
                    //mp3AudioHeader.isVariableBitRate();
                } catch (CannotReadException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TagException e) {
                    throw new RuntimeException(e);
                } catch (ReadOnlyFileException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAudioFrameException e) {
                    throw new RuntimeException(e);
                }
*/
                trackInfo = new TrackInfo(0.0f, selected.getName());


                // Tek dosya ekle
                TrackData additionalTrack = new TrackData.Builder()
                        .withTrackInfo(trackInfo)
                        .withUri(selected.getAbsolutePath())
                        .build();
                //hem m3u liste managere
                m3U8PlaylistManager.addTrackToList(additionalTrack);
                // hem de jlist in listmodeline ekliyoruz.
                listModel.addElement(additionalTrack);

                lastChosenDirectory = selected.getParentFile();
            } else if (selected.isDirectory()) {
                // TODO Klasördeki tüm mp3 ve m4a dosyalarını ekle
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
        for (File selected : files) {
            if (selected.isFile()) {
                String name = selected.getName().toLowerCase();
                if (name.endsWith(".mp3") || name.endsWith(".m4a")) {

                    // Tek dosya ekle
                    TrackData additionalTrack = new TrackData.Builder()
                            .withTrackInfo(new TrackInfo(3.0f, selected.getName()))
                            .withUri(selected.getAbsolutePath())
                            .build();
                    //hem m3u liste managere
                    m3U8PlaylistManager.addTrackToList(additionalTrack);
                    // hem de jlist in listmodeline ekliyoruz.
                    listModel.addElement(additionalTrack);


                }
            }
            // TODO Eğer alt klasörleri de taramak isterseniz,
            //  f.isDirectory() ise recursive çağrı yapabilirsiniz.
        }


    }

    /**
     * "Move Up" butonuna tıklanınca çalışır.
     */
    private void onMoveUp() {
        int listId = playlistList.getSelectedIndex();
        // hem playlistte
        m3U8PlaylistManager.moveTrackUp(listId);
        // hem de jlistte ayni islemi yapiyoruz.
        if (listId > 0 && listId < listModel.size()) {
            // Seçili elemanı bir üst elemanla yer değiştir
            TrackData currentItem = listModel.getElementAt(listId);
            TrackData aboveItem = listModel.getElementAt(listId - 1);

            listModel.setElementAt(aboveItem, listId);
            listModel.setElementAt(currentItem, listId - 1);

            playlistList.setSelectedIndex(listId - 1);
        } else {
            alertNoItemSelected();
        }
    }

    /**
     * "Move Down" butonuna tıklanınca çalışır.
     */
    private void onMoveDown() {
        int listId = playlistList.getSelectedIndex();
        if (listId >= 0 && listId < listModel.size() - 1) {

            // 1) Önce manager içindeki listeyi güncelleyin
            m3U8PlaylistManager.moveTrackDown(listId);

            // 2) Sonra da JList'in modelini güncelleyin
            TrackData currentItem = listModel.getElementAt(listId);
            TrackData belowItem = listModel.getElementAt(listId + 1);

            listModel.setElementAt(belowItem, listId);
            listModel.setElementAt(currentItem, listId + 1);

            playlistList.setSelectedIndex(listId + 1);
        } else {
            alertNoItemSelected();
        }
    }

    /**
     * "Delete" butonuna tıklanınca çalışır.
     */
    private void onDelete() {
        int listId = playlistList.getSelectedIndex();
        if (listId >= 0) {
            listModel.remove(listId);
            m3U8PlaylistManager.removeTrackFromList(listId);
        } else {
            alertNoItemSelected();
        }
    }

    /**
     * "Copy full path" butonuna tıklanınca çalışır.
     */
    private void onCopyFullPath() {

        int listId = playlistList.getSelectedIndex();
        if (listId >= 0 && listId < listModel.size()) {
            String fullPath = listModel.getElementAt(listId).getUri();
            StringSelection selection = new StringSelection(fullPath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        } else {
            alertNoItemSelected();
        }
    }

    /**
     * item index -1 ise yani listede hicbirsey secili degilse calisir.
     */
    private static void alertNoItemSelected() {
        JOptionPane.showMessageDialog(null, "Listede seçili öğe yok veya alt/üst sınıra gelindi", "Uyarı", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * "Open in File Explorer" butonuna tıklanınca çalışır (Windows örneği).
     */
    private void onOpenInExplorer() {

        int index = playlistList.getSelectedIndex();
        if (index >= 0) {
            String fullPath = listModel.getElementAt(index).getUri();
            File file = new File(fullPath);

            FileOpener.openFileLocation(file); // bu sınıfı chatgpt oluşturdu.

        } else {
            alertNoItemSelected();
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
            //  PlayListManagerPanel panel = new PlayListManagerPanel("C:\\tmp\\mp3samples\\playlist1.m3u8");
            PlayListManagerPanel panel = new PlayListManagerPanel();
            frame.getContentPane().add(panel);
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // jlisti getter setterli yaptim cunku playerda next, prev dugmeleri veya ilk acildiginda secili sarki
    // playlistte de secili satir olarak gorunsun diye.
    public JList<TrackData> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(JList<TrackData> playlistList) {
        this.playlistList = playlistList;
    }

    // Seçili playlist değerini döndüren metod


    /*
    public int getSelectedPlaylistItemIndex() {
        return selectedPlaylistItemIndex;
    }

    public void setSelectedPlaylistItemIndex(int selectedPlaylistItemIndex) {
        this.selectedPlaylistItemIndex = selectedPlaylistItemIndex;
    }
    */

    /*
    public JList<TrackData> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(JList<TrackData> playlistList) {
        this.playlistList = playlistList;
    }
    */

    private void changeFrameTitle(String title) {
        // Panelin içindeki frame'i bul
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JDialog) {
            ((JDialog) window).setTitle(title);
        }
        /*
        if (window instanceof JFrame) {
            ((JFrame) window).setTitle(title);
        }

         */
    }
}
