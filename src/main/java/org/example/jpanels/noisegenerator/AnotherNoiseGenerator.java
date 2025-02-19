package org.example.jpanels.noisegenerator;

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
                        // İkinci ve üçüncü harmonikler (temel frekansın katları)
                        double secondHarmonic = Math.sin(2 * sinePhase);
                        double thirdHarmonic = Math.sin(3 * sinePhase);

                        // Harmonik karışım; ağırlıklar isteğe bağlı ayarlanabilir
                        double harmonicValue = baseSine * 0.5 + secondHarmonic * 0.3 + thirdHarmonic * 0.2;

                        // Fazı güncelle
                        sinePhase += phaseIncrement;
                        if (sinePhase > twoPi) {
                            sinePhase -= twoPi;
                        }

                        // Su akışı benzeri gürültü: düşük geçiren filtreli rastgele gürültü
                        if (random.nextDouble() < 0.005) { // yaklaşık her 200 örnekte bir
                            waterTarget = (random.nextDouble() * 2.0) - 1.0; // -1 ile +1 arası
                        }
                        waterNoise += (waterTarget - waterNoise) * waterAlpha;

                        // Amplitüd modülasyonu: yavaş rastgele hedefe doğru geçiş
                        if (random.nextDouble() < 0.001) {
                            ampTarget = 0.7 + (random.nextDouble() * 0.6); // 0.7 ile 1.3 arası
                        }
                        ampMod += (ampTarget - ampMod) * ampAlpha;

                        // Sinyalleri karıştır: harmonik bileşen ile su gürültüsü
                        double sampleValue = (harmonicValue * 0.7 + waterNoise * 0.3) * ampMod;

                        // 16-bit PCM değerine ölçekle
                        int intSample = (int) (sampleValue * 32767);
                        if (intSample > 32767) intSample = 32767;
                        if (intSample < -32768) intSample = -32768;

                        // Little-endian: önce düşük bayt sonra yüksek bayt
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
     * Argüman olarak dakika cinsinden süre bekler.
     * Örneğin: java AudioSynthesizer 5  --> 5 dakika oynatır.
     */
    public static void main(String[] args) {
        int minutes = 5; // varsayılan 1 dakika
        if (args.length > 0) {
            try {
                minutes = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz süre, varsayılan 1 dakika kullanılacak.");
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


    // Mevcut play() metodunuzun yanına ekleyin:
    public void play(long durationMillis) {
        // Öncelikle sesi başlatıyoruz:
        play();  // mevcut play() metodu thread'i başlatıyor

        // Ayrı bir thread'de belirli süre sonra stop() metodunu çağırıyoruz.
        new Thread(() -> {
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop();
        }).start();
    }


}
