package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToggleButtonExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JToggleButton Örneği");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLayout(new FlowLayout());

            JToggleButton toggleButton = new JToggleButton("Kapalı");

            toggleButton.addActionListener(e -> {
                if (toggleButton.isSelected()) {
                    toggleButton.setText("Açık");
                } else {
                    toggleButton.setText("Kapalı");
                }
            });

            frame.add(toggleButton);
            frame.setVisible(true);
        });
    }
}
