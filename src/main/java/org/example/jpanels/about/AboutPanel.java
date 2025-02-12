package org.example.jpanels.about;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ResourceBundle;

public class AboutPanel extends JPanel {
    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    public AboutPanel() {

        setLayout(new GridLayout(10, 1, 5, 5));

        add(new JLabel(bundle.getString("app.name")));
        add(new JLabel(bundle.getString("app.version")));
        add(new JLabel(bundle.getString("app.date")));
        add(new JLabel(bundle.getString("app.description")));
        add(new JLabel(bundle.getString("contact.name")));
       // add(createLinkLabel(bundle.getString("contact.email"), "mailto:" + bundle.getString("contact.email")));
        add(createLinkLabel(bundle.getString("contact.github"), bundle.getString("contact.github")));
       // add(createLinkLabel(bundle.getString("contact.help"), bundle.getString("contact.help")));
       // add(createLinkLabel(bundle.getString("usermanual"), "file:./usermanual.pdf"));
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
