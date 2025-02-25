package org.example.initial;

import org.example.PanelPlugin;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

public class PluginLoader {
    /**
     * Belirtilen jar dosyasından spesifik paneli yükler. Eğer panel bulunamazsa,
     * bilgilendirici bir JPanel döndürür.
     *
     * @param jarFilePath   Yüklenmek istenen jar dosyasının yolu
     * @param panelName     Yüklenmek istenen spesifik panelin adı
     * @return             Panel bulunduysa ilgili JPanel, bulunamazsa bilgilendirme içeren JPanel döner
     */
    public static JPanel loadSpecificPanel(String jarFilePath, String panelName) {
        File jarFile = new File(jarFilePath);

        if (!jarFile.exists()) {
            return createErrorPanel("Jar file not found!", jarFilePath, panelName);
        }

        try {
            // 1️⃣ Jar dosyasını yüklemek için URLClassLoader oluştur
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, PluginLoader.class.getClassLoader());

            // 2️⃣ ServiceLoader kullanarak PanelPlugin implementasyonlarını bul
            ServiceLoader<PanelPlugin> loader = ServiceLoader.load(PanelPlugin.class, classLoader);

            // 3️⃣ İstenen paneli bul ve döndür
            for (PanelPlugin plugin : loader) {
                if (plugin.getTabName().equals(panelName)) {
                    return plugin.getPanel(); // Sadece istenen paneli döndür
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Panel bulunamazsa, bilgilendirici bir JPanel oluştur
        return createErrorPanel("No panel with the specified tag name found!", jarFilePath, panelName);
    }

    /**
     * Panel bulunamazsa bilgilendirici bir JPanel oluşturur.
     *
     * @param message    Kullanıcıya gösterilecek hata mesajı
     * @param jarPath    Yüklenmeye çalışılan jar dosyasının yolu
     * @param panelName  Yüklenmeye çalışılan panelin adı
     * @return          Hata mesajını içeren bir JPanel
     */
    private static JPanel createErrorPanel(String message, String jarPath, String panelName) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5); // Kenarlardan boşluk bırakır

        JLabel messageLabel = new JLabel("<html><b><font size='5'>" + message + "</font></b></html>");
        JLabel jarLabel = new JLabel("<html><font size='5'>Jar File: " + jarPath + "</font></html>");
        JLabel panelLabel = new JLabel("<html><font size='5'>Panel Label: " + panelName + "</font></html>");

        panel.add(messageLabel, gbc);
        panel.add(jarLabel, gbc);
        panel.add(panelLabel, gbc);

        return panel;
    }

    public static void main(String[] args) {
        // Sadece "AboutPanel" olan paneli yükleyelim
        JPanel aboutPanel = loadSpecificPanel("core/mpt-about-ext.jar", "AboutPanel");

        JFrame frame = new JFrame("Dynamic Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(aboutPanel);
        frame.setSize(400, 200);
        frame.setVisible(true);
    }
}
