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

import javax.sound.sampled.*;
import java.util.Random;

public class AnotherNoiseGenerator {

    private volatile boolean playing = false;
    private Thread playThread;
    private SourceDataLine line;

    // Ses formatı parametreleri
    private final float sampleRate = 44100f;
    private final int sampleSizeInBits = 16;
    private final int channels = 1; // mono
    private final boolean signed = true;
    private final boolean bigEndian = false; // little-endian

    // Sentez parametreleri
    private final double schumannFreq = 7.83; // Hz (temel frekans)
    private double sinePhase = 0;
    private final double twoPi = 2 * Math.PI;

    // Su akışı benzeri gürültü için düşük geçiren filtre parametreleri
    private double waterNoise = 0;
    private double waterTarget = 0;
    private final double waterAlpha = 0.002; // yumuşak geçiş için
    private Random random = new Random();

    // Amplitüd modülasyonu için
    private double ampMod = 1.0;
    private double ampTarget = 1.0;
    private final double ampAlpha = 0.0005; // modülasyon hızı

    /**
     * Kullanıcının belirlediği ses seviyesi (dB tabanlı amplitude).
     * 1.0 => 0 dB, daha küçük değerler negatif dB, daha büyük değerler pozitif dB
     * şeklinde düşünebilirsiniz.
     */
    private double volume;
    //private volatile double volume = 50;

    /**
     * Sesi başlatır.
     */
    public void play() {
        playing = true;
        playThread = new Thread(() -> {
            try {
                // Ses formatını oluştur
                AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                // İşlenecek örnek sayısı (buffer boyutu)
                int bufferSamples = 1024;
                byte[] buffer = new byte[bufferSamples * 2]; // 16-bit = 2 byte

                // Örnek başına artış
                double phaseIncrement = twoPi * schumannFreq / sampleRate;

                while (playing) {
                    for (int i = 0; i < bufferSamples; i++) {
                        // Temel Schumann sinüsü
                        double baseSine = Math.sin(sinePhase);
                        // İkinci ve üçüncü harmonikler
                        double secondHarmonic = Math.sin(2 * sinePhase);
                        double thirdHarmonic = Math.sin(3 * sinePhase);

                        // Harmonik karışım
                        double harmonicValue = baseSine * 0.5 + secondHarmonic * 0.3 + thirdHarmonic * 0.2;

                        // Fazı güncelle
                        sinePhase += phaseIncrement;
                        if (sinePhase > twoPi) {
                            sinePhase -= twoPi;
                        }

                        // Su akışı benzeri gürültü
                        if (random.nextDouble() < 0.005) {
                            waterTarget = (random.nextDouble() * 2.0) - 1.0; // -1..+1
                        }
                        waterNoise += (waterTarget - waterNoise) * waterAlpha;

                        // Amplitüd modülasyonu
                        if (random.nextDouble() < 0.001) {
                            ampTarget = 0.7 + (random.nextDouble() * 0.6); // 0.7..1.3
                        }
                        ampMod += (ampTarget - ampMod) * ampAlpha;

                        // Sinyalleri karıştır
                        double sampleValue = (harmonicValue * 0.7 + waterNoise * 0.3) * ampMod;

                        // Kullanıcı ses seviyesi (volume) ile çarparak son değeri belirle
                        sampleValue *= volume;

                        // 16-bit PCM değerine ölçekle
                        int intSample = (int) (sampleValue * 32767);
                        if (intSample > 32767) intSample = 32767;
                        if (intSample < -32768) intSample = -32768;

                        // Little-endian: önce düşük bayt
                        buffer[2 * i] = (byte) (intSample & 0xFF);
                        buffer[2 * i + 1] = (byte) ((intSample >> 8) & 0xFF);
                    }
                    // Buffer'ı ses kartına gönder
                    line.write(buffer, 0, buffer.length);
                }

                // Oynatma sona erdiğinde kaynakları temizle
                line.drain();
                line.stop();
                line.close();

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    /**
     * Sesi durdurur.
     */
    public void stop() {
        playing = false;
        try {
            if (playThread != null) {
                playThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demo için main metodu.
     */
    public static void main(String[] args) {
        int minutes = 5; // varsayılan 5 dakika
        if (args.length > 0) {
            try {
                minutes = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz süre, varsayılan 5 dakika kullanılacak.");
            }
        }
        System.out.println("Oynatma süresi: " + minutes + " dakika.");

        AnotherNoiseGenerator synth = new AnotherNoiseGenerator();
        synth.play();

        // Belirtilen süre boyunca oynat, ardından durdur.
        try {
            Thread.sleep(minutes * 60 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();
        System.out.println("Oynatma durdu.");
    }

    /**
     * Belirli süre çalıp duran ek metot.
     */
    public void play(long durationMillis) {
        play();  // mevcut play() metodu thread'i başlatıyor
        new Thread(() -> {
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop();
        }).start();
    }

    /**
     * 0..100 aralığındaki slider değerini,
     * -80 dB .. 0 dB aralığında bir genliğe (amplitude) dönüştürür.
     */
    /*
    public void setVolume(float volumeSliderValue) {
        // 1) 0..100 --> 0..1
        float linearVolume = volumeSliderValue / 100.0f;
        if (linearVolume < 0.0f) linearVolume = 0.0f;
        if (linearVolume > 1.0f) linearVolume = 1.0f;

        // 2) dB aralığı
        float minDb = -80.0f;
        float maxDb = 0.0f;
        float dB = minDb + (maxDb - minDb) * linearVolume;

        // 3) dB değerini genliğe çevir
        double amplitude = Math.pow(10.0, dB / 20.0);

        // 4) Artık volume, dB dönüştürülmüş amplitude
        this.volume = amplitude;

    }
    */

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }


}
