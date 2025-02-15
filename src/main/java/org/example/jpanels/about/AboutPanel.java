package org.example.jpanels.about;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.util.ResourceBundle;

public class AboutPanel extends JPanel {
    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();


    public AboutPanel() {
        //this.logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png")); // Logonun yolu

        /*
        ImageIcon originalIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        */
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 1, 5, 5));
        // Ust tarafta 20 piksel bsşluk birak
        panel.setBorder(new EmptyBorder(40, 0, 0, 0));
        panel.add(new JLabel(bundle.getString("app.name")));
        panel.add(new JLabel(bundle.getString("app.version")));
        panel.add(new JLabel(bundle.getString("app.date")));
        panel.add(new JLabel(bundle.getString("app.description")));
        panel.add(new JLabel(bundle.getString("contact.name")));
        panel.add(createLinkLabel(bundle.getString("contact.github"), bundle.getString("contact.github")));

        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel logoLabel = new JLabel(logoIcon);
        contentPanel.add(logoLabel, BorderLayout.WEST);
        contentPanel.add(panel, BorderLayout.CENTER);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());


        wrapperPanel.add(contentPanel);



        this.add(wrapperPanel);
    }

    private JLabel createLinkLabel(String text, String url) {
        JLabel label = new JLabel("<html><a href=\"" + url + "\">" + text + "</a></html>");
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return label;
    }
}
