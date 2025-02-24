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
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TapTempoPanel extends JPanel implements PanelPlugin{

    private static final int TOTAL_TAPS = 10;        // 10 tıklamayı sayacağız
    private final JButton tapButton;                // "Tap" butonu
    private final JLabel bpmLabel;                  // BPM değeri gösterilecek Label

    private long[] intervals;                       // Tıklamalar arasındaki süreler
    private int tapCount;                           // Kaçıncı tıklamada olduğumuzu tutar
    private long lastTapTime;                       // Son tık zamanını tutar

    public TapTempoPanel() {
        setLayout(new FlowLayout());

        // Dizi ve sayaç başlangıç değerleri
        intervals = new long[TOTAL_TAPS];
        tapCount = 0;
        lastTapTime = 0;

        // BPM'i göstereceğimiz Label
        bpmLabel = new JLabel("BPM: -");
        add(bpmLabel);

        // "Tap" butonu
        tapButton = new JButton("Tap");
        tapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTap();
            }
        });
        add(tapButton);
    }

    private void handleTap() {
        long currentTime = System.currentTimeMillis();

        // İlk tıklamada sayaç 0 ise sadece zaman başlangıcını kaydediyoruz
        if (tapCount == 0) {
            lastTapTime = currentTime;
            tapCount++;
        }
        else {
            // İkinci ve sonraki tıklamalarda, önceki tıklamadan şu anki tıklamaya kadar geçen süre:
            long interval = currentTime - lastTapTime;
            lastTapTime = currentTime; // Son tıklamayı güncelle

            // Dizide ilgili indeks konuma bu süreyi ekle
            // tapCount bu tıklama için 1'den başladığı için, intervals[tapCount-1] konumu kullanılır
            intervals[tapCount - 1] = interval;
            tapCount++;

            // Eğer 10 tıklamaya ulaştıysak (tapCount == 10 + 1) sonuçları hesapla
            if (tapCount > TOTAL_TAPS) {
                calculateAndDisplayBPM();
                resetTapData();
            }
        }
    }

    private void calculateAndDisplayBPM() {
        // intervals dizisindeki değerlerin ortalamasını bulalım
        long sum = 0;
        for (long interval : intervals) {
            sum += interval;
        }
        double averageInterval = (double) sum / TOTAL_TAPS;

        // BPM = 60.000 ms / averageInterval
        // averageInterval ms'de 1 vuruş (beat) yapılıyor. 1 dakikada (60.000 ms) kaç vuruş yapılır?
        double bpm = 60000.0 / averageInterval;

        // Ekrana yansıtalım
        bpmLabel.setText(String.format("BPM: %.2f", bpm));
    }

    private void resetTapData() {
        tapCount = 0;
        intervals = new long[TOTAL_TAPS];
        lastTapTime = 0;
    }

    // Test amaçlı bir main metodu ile JFrame içerisinde gösterelim:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tap Tempo Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TapTempoPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.taptempo.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}

