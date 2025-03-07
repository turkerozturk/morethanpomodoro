package com.turkerozturk.comboboxes;


public class WavFile {
    private String fileName;  // "dosya.wav"
    private int durationSec;  // Virgülsüz tam saniye
    private int sampleSizeInBits;
    private float sampleRate;

    private int channelCount;

    public WavFile(String fileName, int durationSec, int sampleSizeInBits,
                   float sampleRate, int channelCount) {
        this.fileName = fileName;
        this.durationSec = durationSec;
        this.sampleSizeInBits = sampleSizeInBits;
        this.sampleRate = sampleRate;
        this.channelCount = channelCount;
    }

    public String getFileName() {
        return fileName;
    }

    public int getDurationSec() {
        return durationSec;
    }

    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getChannelCount() {
        return channelCount;
    }

    @Override
    public String toString() {
        // Ekranda nasıl görünecek? Örn: "dosya (12 saniye, 16 bit, 44100 hertz)"
        // Kök isim: uzantıyı atmak için
        String baseName = fileName;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
        }
        return String.format("%s (%d sec, %d bit, %.0f hertz, %d channels)",
                baseName, durationSec, sampleSizeInBits, sampleRate, channelCount);
    }
}
