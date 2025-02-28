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
package com.turkerozturk;

import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTextDisplayPanel extends JPanel{
    private int panelIndex;
    private List<String> messages;
    private JLabel displayLabel;
    private JButton prevButton, nextButton, createFileButton;
    private Timer randomTimer;
    private int currentIndex = 0;
    private boolean isRandom;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private static final ConfigManager config = ConfigManager.getInstance();

    public RandomTextDisplayPanel(int panelIndex) {
        this.panelIndex = panelIndex;
        //loadConfig();
        initPanel();
    }

    /*
    // Config dosyasını yükler.
    private void loadConfig() {
        config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
            // Config dosyası yüklenemezse, kullanıcıya hata gösterilebilir.
        }
    }*/

    private void initPanel() {
        setLayout(new BorderLayout());
        // Font boyutunun dinamik ayarlanabilmesi için başlangıç fontu
        displayLabel = new JLabel("", SwingConstants.CENTER);
        // HTML kullanarak metin wrap sağlanıyor.
        displayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        displayLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(displayLabel, BorderLayout.CENTER);

        // Panelin alt kısmında gezinme butonları eklemek için bir panel
        JPanel buttonPanel = new JPanel();
        // Varsayılan görünümde gezinme butonları görünmeyecek; is.random 0 ise eklenecek.
        prevButton = new JButton("Önceki");
        nextButton = new JButton("Sonraki");
        prevButton.addActionListener(e -> showPreviousMessage());
        nextButton.addActionListener(e -> showNextMessage());
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        // Başlangıçta butonlar gizli
        prevButton.setVisible(false);
        nextButton.setVisible(false);

        add(buttonPanel, BorderLayout.SOUTH);

        // Dosya yolu ve is.random ayarlarını oku
        String fileLocationKey = "txt.file." + panelIndex + ".file.location";
        String isRandomKey = "txt.file." + panelIndex + ".is.random";

        String fileLocation = config.getProperty(fileLocationKey);
        String isRandomStr = config.getProperty(isRandomKey, "0");
        isRandom = "1".equals(isRandomStr.trim());
        //System.out.println(fileLocation);
        File textFile = new File(fileLocation);
        if (!textFile.exists()) {
            // Dosya bulunamadı: oluşturmak için bir buton ve uyarı mesajı göster.
            showMissingFileMessage(textFile);
        } else {
            // Dosya varsa, içeriği oku.
            loadMessagesFromFile(textFile);
            if (messages.isEmpty()) {
                displayLabel.setText("<html><center>Dosya boş veya geçerli satır yok.<br>Lütfen dosyaya metin ekleyiniz.</center></html>");
            } else {
                if (isRandom) {
                    // Rastgele modda: her dakika rastgele mesaj göster.
                    displayRandomMessage();
                    randomTimer = new Timer(60000, e -> displayRandomMessage());
                    randomTimer.start();
                } else {
                    // Manuel mod: ilk mesajı göster ve gezinme butonlarını aktif et.
                    currentIndex = 0;
                    displayMessage(messages.get(currentIndex));
                    prevButton.setVisible(true);
                    nextButton.setVisible(true);
                }
            }
        }

        // Panel boyutu değiştiğinde fontu yeniden boyutlandırmak için dinleyici ekle
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFont();
            }
        });
    }

    // Dosya yoksa gösterilecek uyarı ve dosyayı oluşturacak buton
    private void showMissingFileMessage(File textFile) {
        removeAll();
        setLayout(new BorderLayout());
        JLabel warningLabel = new JLabel("<html><center>Belirtilen dosya bulunamadı.<br>Lütfen <b>" + textFile.getName() + "</b> dosyasını oluşturup içine birkaç satır metin yazınız.<br>Uygulamayı tekrar başlatınız.</center></html>", SwingConstants.CENTER);
        add(warningLabel, BorderLayout.CENTER);

        createFileButton = new JButton("Dosyayı Oluştur");
        createFileButton.addActionListener(e -> {
            try {
                // Dosyayı oluştur ve örnek içerik yaz.
                if (textFile.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(textFile), StandardCharsets.UTF_8))) {
                        writer.println("Örnek metin satırı 1");
                        writer.println("Örnek metin satırı 2");
                    }
                    JOptionPane.showMessageDialog(this, textFile.getName() + " dosyası oluşturuldu.\nLütfen dosyaya kendi metinlerinizi ekleyin ve uygulamayı yeniden başlatın.");
                } else {
                    JOptionPane.showMessageDialog(this, "Dosya oluşturulamadı.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        JPanel btnPanel = new JPanel();
        btnPanel.add(createFileButton);
        add(btnPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // Dosyadan mesajları oku (UTF-8 ile) ve '#' ile başlayan satırları atla.
    private void loadMessagesFromFile(File textFile) {
        messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("#") && !line.trim().isEmpty()) {
                    messages.add(line.trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Rastgele bir mesaj göster.
    private void displayRandomMessage() {
        if (messages != null && !messages.isEmpty()) {
            Random rand = new Random();
            int idx = rand.nextInt(messages.size());
            displayMessage(messages.get(idx));
        }
    }

    // Mesajı label üzerinde HTML ile wrap olacak şekilde göster.
    private void displayMessage(String msg) {
        // HTML etiketleri kullanarak metnin wrap olmasını sağlıyoruz.
        displayLabel.setText("<html><div style='text-align: center;'>" + msg + "</div></html>");
        resizeFont();
    }

    // Manuel modda önceki mesajı göster.
    private void showPreviousMessage() {
        if (messages == null || messages.isEmpty()) return;
        currentIndex = (currentIndex - 1 + messages.size()) % messages.size();
        displayMessage(messages.get(currentIndex));
    }

    // Manuel modda sonraki mesajı göster.
    private void showNextMessage() {
        if (messages == null || messages.isEmpty()) return;
        currentIndex = (currentIndex + 1) % messages.size();
        displayMessage(messages.get(currentIndex));
    }

    // Panelin boyutuna göre font boyutunu ayarlar.
    private void resizeFont() {
        // Örneğin panel yüksekliğinin %5'i kadar bir font boyutu kullanılabilir.
        int fontSize = Math.max(12, getHeight() / 20);
        displayLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
    }

    // Panel kapatılırken timer'ı durdurmak iyi olur.
    public void stopTimer() {
        if (randomTimer != null && randomTimer.isRunning()) {
            randomTimer.stop();
        }
    }

    // Örnek test için main metodu (bağımsız çalıştırılabilir)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RandomTextDisplayPanel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Örneğin tabbed pane ile 3 panel ekleniyor.
            JTabbedPane tabbedPane = new JTabbedPane();

            // Her panel için display name config'ten okunabilir.
            for (int i = 1; i <= 3; i++) {
                String displayName = "txt.file." + i + ".display.name";
                String tabName = "Panel " + i;
                /*
                if (frameExistsProperty(displayName)) {
                    tabName = frameGetProperty(displayName);
                }
                */

                RandomTextDisplayPanel panel = new RandomTextDisplayPanel(i);
                tabbedPane.addTab(tabName, panel);
            }

            frame.add(tabbedPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }



    /*
    // Helper metotlar: config içerisinden display name almak için (main içindeki örnek kullanımda)
    private static boolean frameExistsProperty(String key) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props.getProperty(key) != null;
    }*/

    /*
    private static String frameGetProperty(String key) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props.getProperty(key);
    }
    */
}
