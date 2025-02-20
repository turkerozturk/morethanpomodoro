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

        JTabbedPane jTabbedPaneAbout = new JTabbedPane();
        //this.logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png")); // Logonun yolu

        /*
        ImageIcon originalIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledImage);
        */
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));


        JPanel aboutMySoftwarePanel = new JPanel();
        aboutMySoftwarePanel.setLayout(new GridLayout(15, 1, 5, 5));
        // Ust tarafta 20 piksel bsşluk birak
        //panel.setBorder(new EmptyBorder(40, 0, 0, 0));

        aboutMySoftwarePanel.add(new JLabel(bundle.getString("app.name")));
        aboutMySoftwarePanel.add(new JLabel(bundle.getString("app.version")));
        aboutMySoftwarePanel.add(new JLabel(bundle.getString("app.date")));
        aboutMySoftwarePanel.add(new JLabel(bundle.getString("app.description")));
        aboutMySoftwarePanel.add(new JLabel(bundle.getString("contact.name")));
        aboutMySoftwarePanel.add(createLinkLabel(bundle.getString("contact.github"), bundle.getString("contact.github")));
        aboutMySoftwarePanel.add(new JLabel(bundle.getString("app.help.description")));
        aboutMySoftwarePanel.add(new JLabel("License: GPL-3.0"));



        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel logoLabel = new JLabel(logoIcon);
        contentPanel.add(logoLabel, BorderLayout.WEST);
        contentPanel.add(aboutMySoftwarePanel, BorderLayout.CENTER);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(contentPanel);




        jTabbedPaneAbout.addTab("About MoreThanPomodoro", wrapperPanel);


        JPanel aboutThirdPartyLibrariesPanel = new JPanel();
        aboutThirdPartyLibrariesPanel.setLayout(new GridLayout(15, 1, 5, 5));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: FlatLaf, Apache 2.0"));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: flatlaf-intellij-themes, Apache 2.0"));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: jfugue, Apache 2.0"));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: jdatepicker, Simplified BSD"));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: openjfx, GPL-2.0"));

        aboutThirdPartyLibrariesPanel.add(new JLabel("Library: commons-suncalc, Apache 2.0"));

        jTabbedPaneAbout.addTab("About 3. Party Libraries", aboutThirdPartyLibrariesPanel);







        this.add(jTabbedPaneAbout);
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
