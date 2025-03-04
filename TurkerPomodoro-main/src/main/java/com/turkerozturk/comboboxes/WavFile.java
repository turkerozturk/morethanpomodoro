package com.turkerozturk.comboboxes;


public class WavFile {
    private String fileName;  // "dosya.wav"
    private int durationSec;  // Virgülsüz tam saniye
    private int sampleSizeInBits;
    private float sampleRate;

    public WavFile(String fileName, int durationSec, int sampleSizeInBits, float sampleRate) {
        this.fileName = fileName;
        this.durationSec = durationSec;
        this.sampleSizeInBits = sampleSizeInBits;
        this.sampleRate = sampleRate;
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

    @Override
    public String toString() {
        // Ekranda nasıl görünecek? Örn: "dosya (12 saniye, 16 bit, 44100 hertz)"
        // Kök isim: uzantıyı atmak için
        String baseName = fileName;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
        }
        return String.format("%s (%d sec, %d bit, %.0f hertz)",
                baseName, durationSec, sampleSizeInBits, sampleRate);
    }
}
