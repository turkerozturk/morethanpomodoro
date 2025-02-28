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
import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class AboutPanel extends JPanel implements PanelPlugin{
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

    @Override
    public String getTabName() {
        return "plugin.about.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.CORE;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
