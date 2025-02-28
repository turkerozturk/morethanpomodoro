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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ConfigurationEditorPanel extends JPanel implements PanelPlugin {

    private static final String CONFIG_FILE_NAME = "config.properties";

    private File configFile;
    private Properties properties;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField filterField;
    private JButton saveButton;

    public ConfigurationEditorPanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        configFile = new File(System.getProperty("user.dir"), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            removeAll();
            JPanel noFilePanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel messageLabel = new JLabel("Configuration file not found!");
            noFilePanel.add(messageLabel, gbc);

            gbc.gridy++;
            JButton createButton = new JButton("Create config file");
            createButton.addActionListener(e -> {
                copyConfigFromResources();
                readPropertiesAndSetupUI();
            });
            noFilePanel.add(createButton, gbc);

            add(noFilePanel, BorderLayout.CENTER);
        } else {
            readPropertiesAndSetupUI();
        }

        revalidate();
        repaint();
    }

    /**
     * Kopyalama işlemi: Default config dosyasını resources'tan uygulama klasörüne kopyalar.
     */
    private void copyConfigFromResources() {
        URL defaultConfigURL = getClass().getResource("/" + CONFIG_FILE_NAME);
        if (defaultConfigURL == null) {
            JOptionPane.showMessageDialog(this, "Could not find default configuration in resources.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (InputStream in = defaultConfigURL.openStream()) {
            Files.copy(in, configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to copy config file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Config dosyasını okuyup UI'ı oluşturur.
     */
    private void readPropertiesAndSetupUI() {
        removeAll();
        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading configuration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Properties'i alfabetik sıraya koymak için TreeMap kullanıyoruz
        TreeMap<String, String> sortedProps = new TreeMap<>();
        for (String key : properties.stringPropertyNames()) {
            sortedProps.put(key, properties.getProperty(key));
        }

        // Table model: ilk sütun key (düzenlenemez), ikinci sütun value (düzenlenebilir)
        tableModel = new DefaultTableModel(new Object[]{"Key", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        // Sıralı anahtarları tabloya ekleyelim
        for (Map.Entry<String, String> entry : sortedProps.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Value hücresi için uygun metin alanını oluşturuyoruz.
        // IntelliJ altında çalışıyorsak NoPasteTextField, değilse normal JTextField kullanıyoruz.
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(createValueTextField()));

        // Üst kısımda filtreleme için metin kutusu ekleyelim.
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        filterField = new JTextField();
        filterPanel.add(filterField, BorderLayout.CENTER);

        // Arama metnine göre filtreleme
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter(); }

            private void updateFilter() {
                String text = filterField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    // Sadece key sütununda arama yapıyoruz (düzenli ifade, büyük/küçük harf duyarsız)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveProperties());

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Uygulamanın IntelliJ üzerinden çalıştırılıp çalıştırılmadığını kontrol eder.
     */
    private boolean isRunningInIntelliJ() {
        // IntelliJ genellikle "idea_rt.jar" içerir veya "idea.launcher.port" sistem özelliğine sahiptir.
        String classPath = System.getProperty("java.class.path");
        return System.getProperty("idea.launcher.port") != null ||
                (classPath != null && classPath.contains("idea_rt.jar"));
    }

    /**
     * Value hücresi için kullanılacak metin alanını oluşturur.
     * IntelliJ altında çalışıyorsak paste işlemini devre dışı bırakan NoPasteTextField kullanılır.
     */
    private JTextField createValueTextField() {
        if (isRunningInIntelliJ()) {
            return new NoPasteTextField(50);
        } else {
            return new JTextField(50);
        }
    }

    /**
     * Config dosyasını güncel değerlerle kaydeder.
     */
    private void saveProperties() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String key = tableModel.getValueAt(i, 0).toString();
            String value = tableModel.getValueAt(i, 1).toString();
            properties.setProperty(key, value);
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Updated Configuration");
            JOptionPane.showMessageDialog(this, "Configuration saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving configuration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Test için paneli bir JFrame içerisinde çalıştıralım.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Configuration Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new ConfigurationEditorPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.configuration.editor.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
