package org.example.jpanels.configuration;

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

public class ConfigurationEditorPanel extends JPanel {

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
     * Copies the default config file from resources to the application folder.
     */
    private void copyConfigFromResources() {
        URL defaultConfigURL = getClass().getResource("/" + CONFIG_FILE_NAME);
        if (defaultConfigURL == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not find default configuration in resources.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (InputStream in = defaultConfigURL.openStream()) {
            Files.copy(in, configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to copy config file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads properties from the configFile and sets up the UI.
     */
    private void readPropertiesAndSetupUI() {
        removeAll();
        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error reading configuration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ortaya çıkan verileri alfabetik olarak sıralamak için TreeMap kullanıyoruz
        TreeMap<String, String> sortedProps = new TreeMap<>();
        for (String key : properties.stringPropertyNames()) {
            sortedProps.put(key, properties.getProperty(key));
        }

        // Table model oluşturma: ilk sütun key (değiştirilemez), ikinci sütun value (düzenlenebilir)
        tableModel = new DefaultTableModel(new Object[]{"Key", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Sadece value sütunu düzenlenebilir
                return column == 1;
            }
        };

        // Sıralı anahtarları table model'e ekle
        for (Map.Entry<String, String> entry : sortedProps.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        // JTable oluşturma
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        // TableRowSorter ile sütun başlıklarına tıklayarak sıralama yapılabilir
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Filter için metin kutusu ekleyelim
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        filterField = new JTextField();
        filterPanel.add(filterField, BorderLayout.CENTER);

        // Arama kutusuna girilen metni dinleyerek filtre uygulayalım
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }

            private void updateFilter() {
                String text = filterField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Sadece key sütununda (index 0) arama yapıyoruz
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                }
            }
        });

        // ScrollPane içine JTable ekleyelim
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Save butonu
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveProperties());

        // Ana panel düzeni
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Overwrites the configFile with updated properties from the table.
     */
    private void saveProperties() {
        // Tablo modelinde yer alan her satırdaki key-value değerlerini properties'e aktar
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
            JOptionPane.showMessageDialog(this,
                    "Error saving configuration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // For testing, run this panel in a simple JFrame
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
}
