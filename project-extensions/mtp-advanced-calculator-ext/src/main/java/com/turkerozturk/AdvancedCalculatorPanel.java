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

import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.thirdparty.calculator.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URI;

/**
 * Hello world!
 *
 */
public class AdvancedCalculatorPanel extends JPanel implements PanelPlugin
{
    private static final String CODE_OWNER = "Code Owner: Swif7ify";
    private static final String LICENSE = "License: MIT License";
    private static final String GITHUB_URL = "https://github.com/Swif7ify/Advanced-Calculator-Made-in-Java-Swing";

    public AdvancedCalculatorPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 150));

        // Kod sahibinin adı
        JLabel ownerLabel = new JLabel(CODE_OWNER);
        ownerLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Lisans
        JLabel licenseLabel = new JLabel(LICENSE);
        licenseLabel.setAlignmentX(LEFT_ALIGNMENT);

        // GitHub URL (tıklanabilir)
        JLabel githubLabel = new JLabel("<html><a href='#'>" + GITHUB_URL + "</a></html>");
        githubLabel.setAlignmentX(LEFT_ALIGNMENT);
        githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Linke tıklanınca tarayıcıda açmayı dene
                try {
                    Desktop.getDesktop().browse(new URI(GITHUB_URL));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // "Open Piano" butonu
        JButton openPianoButton = new JButton("Open Advanced Calculator");
        openPianoButton.setAlignmentX(LEFT_ALIGNMENT);
        openPianoButton.addActionListener(e -> {

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Main frame = new Main();
                        frame.setTitle("Calculator But Its Not A Normal Calculator It Is A Calculator That Can Literally Solve Human Crisis");
                        InputStream imageStream = getClass().getResourceAsStream("/Picture/teamba.png");
                        Image image = ImageIO.read(imageStream);
                        frame.setIconImage(image);
                        frame.setVisible(true);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        });

        // Panellere ekleme
        add(Box.createVerticalStrut(10));
        add(ownerLabel);
        add(Box.createVerticalStrut(5));
        add(licenseLabel);
        add(Box.createVerticalStrut(5));
        add(githubLabel);
        add(Box.createVerticalStrut(10));
        add(openPianoButton);
        add(Box.createVerticalGlue());
    }


    // Kısa bir test için main metodu (isteğe bağlı)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MidiPiano Info");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new AdvancedCalculatorPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }



    @Override
    public String getTabName() {
        return "plugin.advanced.calculator.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.PRODUCTIVITY;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
