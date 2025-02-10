package org.example.jpanels.taptempo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TapTempoTool extends JPanel {

    private static final int TOTAL_TAPS = 10;        // 10 tıklamayı sayacağız
    private final JButton tapButton;                // "Tap" butonu
    private final JLabel bpmLabel;                  // BPM değeri gösterilecek Label

    private long[] intervals;                       // Tıklamalar arasındaki süreler
    private int tapCount;                           // Kaçıncı tıklamada olduğumuzu tutar
    private long lastTapTime;                       // Son tık zamanını tutar

    public TapTempoTool() {
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
            frame.setContentPane(new TapTempoTool());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

