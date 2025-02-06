package org.example;

import javax.swing.*;
import java.awt.*;

public class SpinnerExample {
    public static void main(String[] args) {
        // Swing bileşenlerini ana thread içinde çalıştır
        SwingUtilities.invokeLater(() -> {
            // Yeni bir pencere oluştur
            JFrame frame = new JFrame("JSpinner Örneği");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLayout(new FlowLayout());

            // Spinner için bir model oluştur (Başlangıç: 0, Min: 0, Max: 100, Adım: 1)
            SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
            JSpinner spinner = new JSpinner(spinnerModel);

            // Spinner değerini gösterecek bir etiket
            JLabel label = new JLabel("Seçilen Değer: 0");

            // Spinner değiştiğinde etiketi güncelle
            spinner.addChangeListener(e -> {
                int value = (Integer) spinner.getValue();
                label.setText("Seçilen Değer: " + value);
            });

            // Bileşenleri pencereye ekle
            frame.add(spinner);
            frame.add(label);

            // Pencereyi görünür yap
            frame.setVisible(true);
        });
    }
}
