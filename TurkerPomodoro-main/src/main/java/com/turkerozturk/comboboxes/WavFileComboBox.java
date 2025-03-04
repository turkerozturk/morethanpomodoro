package com.turkerozturk.comboboxes;



import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.*;

public class WavFileComboBox extends JComboBox<WavFile> {

    public WavFileComboBox() {
        // Maksimum genişlik belirle (örnek 150px)
        this.setMaximumSize(new Dimension(150, 25));
        // Opsiyonel: preferredSize
        this.setPreferredSize(new Dimension(150, 25));
    }

    // JComboBox öğelerini ticksounds klasöründeki wav dosyalarına göre doldurur.
    public void populateComboBox() {
        removeAllItems(); // Temizleyelim ki tekrar çağrıldığında eklenmesin.

        File currentDir = new File(System.getProperty("user.dir"));
        File ticksoundsFolder = new File(currentDir, "ticksounds");
        if (!ticksoundsFolder.exists() || !ticksoundsFolder.isDirectory()) {
            System.out.println("ticksounds klasörü bulunamadı.");
            return;
        }

        // Sadece .wav uzantılı dosyalar filtreleniyor
        File[] wavFiles = ticksoundsFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }
        });

        if (wavFiles == null || wavFiles.length == 0) {
            System.out.println("ticksounds klasöründe wav dosyası bulunamadı.");
            return;
        }

        // Alfabetik sıralama
        List<File> filesList = new ArrayList<>(Arrays.asList(wavFiles));
        filesList.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

        // Örnek: "tick.wav" her zaman en başta olsun isterseniz (opsiyonel):
        // File tickFile = new File(ticksoundsFolder, "tick.wav");
        // if (tickFile.exists()) {
        //     filesList.removeIf(file -> file.getName().equalsIgnoreCase("tick.wav"));
        //     addItem(createWavFile(tickFile));
        // }

        // Kalan dosyaları ekleyelim
        for (File file : filesList) {
            addItem(createWavFile(file));
        }
    }

    // File'dan WavFile nesnesi oluşturur
    private WavFile createWavFile(File file) {
        // Default değerler
        int durationSec = 0;
        int bits = 0;
        float rate = 0f;

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = ais.getFormat();
            long frameLength = ais.getFrameLength();
            float frameRate = format.getFrameRate();

            // Süreyi bul (virgülden sonraki kısmı at => int)
            double durationSeconds = frameLength / frameRate;
            durationSec = (int) durationSeconds;  // Küsuratsız saniye

            bits = format.getSampleSizeInBits();
            rate = format.getSampleRate();

        } catch (Exception e) {
            System.err.println("WAV özellikleri okunamadı: " + file.getName());
        }

        return new WavFile(file.getName(), durationSec, bits, rate);
    }

    // Seçilen WAV dosyasının WavFile nesnesini almak
    public WavFile getSelectedWavFile() {
        return (WavFile) getSelectedItem();
    }
}
