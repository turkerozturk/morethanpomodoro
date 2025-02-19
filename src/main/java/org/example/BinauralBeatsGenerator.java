package org.example;

import javax.sound.sampled.*;

public class BinauralBeatsGenerator implements Runnable {
    private static final int SAMPLE_RATE = 44100; // CD kalitesi

    private volatile boolean isPlaying = false;
    private volatile boolean stopRequested = false;
    private Thread soundThread;

    private double baseFrequency;
    private double beatFrequency;
    private volatile float volume; // Ses seviyesi (0.0 ile 1.0 arasında)

    public BinauralBeatsGenerator(double baseFrequency, double beatFrequency, float volume) {
        this.baseFrequency = baseFrequency;
        this.beatFrequency = beatFrequency;
        setVolume(volume); // Ses seviyesini sınırlayarak başlat
    }

    public synchronized void start() {
        if (isPlaying) {
            return; // Zaten çalışıyorsa tekrar başlatma
        }
        stopRequested = false;
        soundThread = new Thread(this);
        soundThread.start();
    }

    public synchronized void stop() {
        stopRequested = true;
    }

    public void setVolume(float volumeSliderValue) {
        // 0..100 arasındaki slider değerini 0..1 aralığına indir
        float linearVolume = volumeSliderValue / 100.0f;
        if (linearVolume < 0.0f) {
            linearVolume = 0.0f;
        } else if (linearVolume > 1.0f) {
            linearVolume = 1.0f;
        }

        // dB aralığını belirleyin (örnek: -50 dB ile 0 dB arası)
        float minDb = -80.0f;
        float maxDb = 0.0f;

        // lineer [0..1] -> dB [minDb..maxDb]
        // volume=0 -> -50 dB, volume=1 -> 0 dB
        float dB = minDb + (maxDb - minDb) * linearVolume;
        // dB -> genlik (amplitude)
        float amplitude = (float) Math.pow(10.0, dB / 20.0);

        this.volume = amplitude;
    }


    @Override
    public void run() {
        isPlaying = true;
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            int bufferSize = SAMPLE_RATE * 4; // 1 saniyelik ses verisi (16-bit, stereo)
            byte[] audioBuffer = new byte[bufferSize];
            int sampleIndex = 0;

            while (!stopRequested) {
                for (int i = 0; i < bufferSize / 4; i++) {
                    double time = (double) sampleIndex / SAMPLE_RATE;

                    // Sol kanal (baseFrequency)
                    double leftSample = Math.sin(2 * Math.PI * baseFrequency * time);

                    // Sağ kanal (baseFrequency + beatFrequency)
                    double rightSample = Math.sin(2 * Math.PI * (baseFrequency + beatFrequency) * time);

                    // Ses seviyesi ayarlama
                    leftSample *= volume;
                    rightSample *= volume;

                    // 16-bit PCM dönüşümü
                    short leftValue = (short) (leftSample * Short.MAX_VALUE);
                    short rightValue = (short) (rightSample * Short.MAX_VALUE);

                    int index = i * 4;
                    audioBuffer[index] = (byte) (leftValue & 0xff);
                    audioBuffer[index + 1] = (byte) ((leftValue >> 8) & 0xff);
                    audioBuffer[index + 2] = (byte) (rightValue & 0xff);
                    audioBuffer[index + 3] = (byte) ((rightValue >> 8) & 0xff);

                    sampleIndex++;
                }

                line.write(audioBuffer, 0, audioBuffer.length);
            }

            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    public static void main(String[] args) throws InterruptedException {
        BinauralBeatsGenerator generator = new BinauralBeatsGenerator(528, 5, 1);

        System.out.println("Başlatılıyor...");
        generator.start();

        Thread.sleep(5000); // 5 saniye sonra durdur
        System.out.println("Durduruluyor...");
        generator.stop();

        Thread.sleep(2000); // 2 saniye bekle
        System.out.println("Tekrar başlatılıyor...");
        generator.start();

        Thread.sleep(5000); // 5 saniye sonra tamamen durdur
        System.out.println("Tamamen durduruluyor...");
        generator.stop();
    }

    public double getBaseFrequency() {
        return baseFrequency;
    }

    public void setBaseFrequency(double baseFrequency) {
        this.baseFrequency = baseFrequency;
    }

    public double getBeatFrequency() {
        return beatFrequency;
    }

    public void setBeatFrequency(double beatFrequency) {
        this.beatFrequency = beatFrequency;
    }

    public float getVolume() {
        volume = volume * 100; // guideki slider 0 - 100 ve buradaki volume 0.0 ile 1.0 arasinda oldugundan carmpa islemi yapiyoruz.
        return volume;
    }
}
