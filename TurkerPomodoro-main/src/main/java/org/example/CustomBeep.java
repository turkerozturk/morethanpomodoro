package org.example;

import javax.sound.sampled.*;

public class CustomBeep {

    public static void generateTone(double frequency, int durationMs, boolean squareWave, double volume) {
        try {
            float sampleRate = 44100; // Standart ses örnekleme frekansı (Hz)
            int numSamples = (int) ((durationMs / 1000.0) * sampleRate);
            byte[] buffer = new byte[numSamples];

            // Volume ayarı için normalleştirme (0.0 - 1.0 arasında olmalı)
            volume = Math.max(0.0, Math.min(1.0, volume));
            int amplitude = (int) (127 * volume); // Ses seviyesi 0 - 127 arasında ayarlanıyor

            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;

                if (squareWave) {
                    buffer[i] = (byte) (Math.signum(Math.sin(angle)) * amplitude); // Kare dalga
                } else {
                    buffer[i] = (byte) (Math.sin(angle) * amplitude); // Sinüs dalgası
                }
            }

            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Sinüs dalgası çalıyor...");
        generateTone(1000, 3000, false, 0.5); // 1000 Hz, 3000 ms, %50 ses seviyesi

        System.out.println("Kare dalga çalıyor...");
        generateTone(1000, 3000, true, 0.8); // 1000 Hz, 3000 ms, %80 ses seviyesi
    }
}
