package org.example;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PomodoroFrame().setVisible(true);
        });
    }}
