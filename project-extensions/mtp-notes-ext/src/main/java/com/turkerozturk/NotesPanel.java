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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotesPanel extends JPanel implements PanelPlugin{

    private static final String NOTES_FILE = "notes.txt";

    private JButton createNotesButton;
    private JTextArea notesArea;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JCheckBox autoSaveCheckbox;
    private JButton saveButton;

    private boolean contentChanged = false;
    private Timer autoSaveTimer;

    public NotesPanel() {
        setLayout(new BorderLayout(10, 10));
        File notesFile = new File(NOTES_FILE);

        if (!notesFile.exists()) {
            // Dosya yok ise \"Create notes.txt\" butonu göster
            createNotesButton = new JButton("Create notes.txt");
            createNotesButton.addActionListener(e -> {
                try {
                    if (notesFile.createNewFile()) {
                        // Dosya oluşturulursa butonu kaldır ve ana bileşenleri ekle
                        remove(createNotesButton);
                        initNotesComponents();
                        revalidate();
                        repaint();
                    }
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this,
                            "notes.txt oluşturulamadı!",
                            "Hata",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            add(createNotesButton, BorderLayout.CENTER);
        } else {
            // Zaten dosya varsa, direkt bileşenleri göster
            initNotesComponents();
        }
    }

    private void initNotesComponents() {
        // Metin alanı
        notesArea = new JTextArea(10, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        // Var olan dosyadan içerik yükle
        loadNotesFromFile();

        // Metin alanına DocumentListener ekleyerek değişiklikleri izleyin
        Document doc = notesArea.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onContentChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onContentChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onContentChanged(); }
        });

        // Kaydırma çubuklu panel
        scrollPane = new JScrollPane(
                notesArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        // Durum etiketi
        statusLabel = new JLabel();

        // Otomatik kaydetme için checkbox
        autoSaveCheckbox = new JCheckBox("Auto Save (10s)");
        autoSaveCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoSaveTimer != null) {
                    autoSaveTimer.stop();
                }
            }
        });

        // Kaydet butonu
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNotesToFile();
            }
        });

        // Alt panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.add(autoSaveCheckbox);
        bottomPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onContentChanged() {
        contentChanged = true;
        statusLabel.setText("Content Changed!");
        if (autoSaveCheckbox.isSelected()) {
            scheduleAutoSave();
        }
    }

    private void scheduleAutoSave() {
        // Eğer önceki bir zamanlayıcı varsa kapat
        if (autoSaveTimer != null) {
            autoSaveTimer.stop();
        }
        // 10 saniye sonra kaydetme işlemini tetikler
        autoSaveTimer = new Timer(10_000, e -> {
            if (contentChanged) {
                saveNotesToFile();
            }
        });
        autoSaveTimer.setRepeats(false);
        autoSaveTimer.start();
    }

    private void loadNotesFromFile() {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(NOTES_FILE));
            String content = new String(fileBytes);
            notesArea.setText(content);
        } catch (IOException e) {
            notesArea.setText("");
        }
    }

    private void saveNotesToFile() {
        try (FileWriter writer = new FileWriter(NOTES_FILE)) {
            writer.write(notesArea.getText());
            writer.flush();
            contentChanged = false;

            // Tarih formatı => yyyy-MM-dd HH:mm:ss
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            statusLabel.setText("Saved on " + timestamp);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Kaydetme hatası!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String getTabName() {
        return "plugin.notes.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    /*

    // Test amaçlı main metodu:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\"NotesPanel Test\");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new NotesPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    */
}
