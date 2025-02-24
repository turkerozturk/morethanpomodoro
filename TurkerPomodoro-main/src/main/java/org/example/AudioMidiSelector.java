package org.example;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AudioMidiSelector extends JFrame {
    private JComboBox<Mixer.Info> audioOutputCombo;
    private JComboBox<MidiDevice.Info> midiOutputCombo;
    private JButton playAudioButton, playMidiButton;

    public AudioMidiSelector() {
        setTitle("Ses ve MIDI Aygıt Seçici");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // Ses Aygıtlarını Listele
        audioOutputCombo = new JComboBox<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            audioOutputCombo.addItem(mixerInfo);
        }
        add(new JLabel("Ses Çıkış Aygıtları:"));
        add(audioOutputCombo);

        // MIDI Aygıtlarını Listele
        midiOutputCombo = new JComboBox<>();
        MidiDevice.Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info midiInfo : midiDevices) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(midiInfo);
                if (device.getMaxReceivers() != 0) { // Sadece çıkış destekleyen MIDI aygıtlarını ekle
                    midiOutputCombo.addItem(midiInfo);
                }
            } catch (MidiUnavailableException ignored) {
            }
        }
        add(new JLabel("MIDI Çıkış Aygıtları:"));
        add(midiOutputCombo);

        // Ses Çalma Butonu
        playAudioButton = new JButton("Ses Çal");
        playAudioButton.addActionListener(e -> playSampleAudio());
        add(playAudioButton);

        // MIDI Çalma Butonu
        playMidiButton = new JButton("MIDI Çal");
        playMidiButton.addActionListener(e -> playSampleMidi());
        add(playMidiButton);

        // Aygıt Yoksa Uyarı Göster
        if (audioOutputCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Sisteminizde ses çıkış aygıtı bulunamadı!", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
        if (midiOutputCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Sisteminizde MIDI çıkış aygıtı bulunamadı!", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }

        setVisible(true);
    }

    // Örnek Ses Çalma Metodu
    private void playSampleAudio() {
        File soundFile = new File("sample.wav"); // Kendi WAV dosyanızı buraya ekleyin
        if (!soundFile.exists()) {
            JOptionPane.showMessageDialog(this, "Örnek ses dosyası bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Örnek MIDI Çalma Metodu
    private void playSampleMidi() {
        if (midiOutputCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir MIDI çıkış aygıtı seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            MidiDevice.Info selectedMidiInfo = (MidiDevice.Info) midiOutputCombo.getSelectedItem();
            MidiDevice midiDevice = MidiSystem.getMidiDevice(selectedMidiInfo);
            midiDevice.open();

            Receiver receiver = midiDevice.getReceiver();
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93); // Orta C notası
            receiver.send(msg, -1);

            Thread.sleep(1000); // 1 saniye beklet

            msg.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0); // Notayı kapat
            receiver.send(msg, -1);

            midiDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AudioMidiSelector::new);
    }
}

