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
package org.example.sounds;

import javax.sound.sampled.*;

public class AsyncBeep {

    public static void generateToneAsync(double frequency, int durationMs, boolean squareWave, double finalVolume) {
      //  final double finalVolume = Math.max(0.0, Math.min(1.0, volume)); // Yeni final değişken

        new Thread(() -> {
            try {
                float sampleRate = 44100; // Standart ses örnekleme frekansı (Hz)
                int numSamples = (int) ((durationMs / 1000.0) * sampleRate);
                byte[] buffer = new byte[numSamples];

                int amplitude = (int) (127 * (finalVolume / 100) ); // Ses seviyesi 0 - 127 arasında ayarlanıyor,
                // volume 0 ile 1 arasinda oldugundan, 0-100luk parametreyi 100e bolerek donusturduk.

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
        }).start();
    }

    public static void main(String[] args) {
        System.out.println("Uygulama devam ediyor...");

        // 1000 Hz, 5000 ms, %50 ses seviyesi ile asenkron sinüs dalgası
        generateToneAsync(1000, 5000, false, 50);

        // 2 saniye bekleyip başka bir ses çalalım (ana thread etkilenmez)
        try { Thread.sleep(2000); } catch (InterruptedException ignored) { }
        generateToneAsync(800, 5000, true, 100);

        System.out.println("Uygulama hala çalışıyor...");
    }
}
