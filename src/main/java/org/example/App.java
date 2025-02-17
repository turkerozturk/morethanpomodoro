package org.example;


import org.example.initial.ConfigManager;
import org.example.initial.ThemeManager;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final ConfigManager props = ConfigManager.getInstance();
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ApplicationFrame applicationFrame = new ApplicationFrame();
            ThemeManager.loadTheme();

            applicationFrame.setVisible(true);
        });
    }}
