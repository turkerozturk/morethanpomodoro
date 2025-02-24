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
