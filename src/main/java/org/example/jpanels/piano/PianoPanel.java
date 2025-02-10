package org.example.jpanels.piano;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PianoPanel extends JPanel {

    private static final String[] NOTE_NAMES = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };
    private static final int[] NOTE_KEYS = {
            60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71 // Orta C (C4) ile B4
    };
    private static final int DEFAULT_INSTRUMENT = 0; // Piyano
    private JSlider volumeSlider;

    private Synthesizer synthesizer;
    private MidiChannel channel;
    private JComboBox<String> instrumentSelector;

    public PianoPanel() {
        setLayout(new BorderLayout());

        // MIDI başlatma
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            return;
        }

        // Üst panelde enstrüman seçici
        JPanel topPanel = new JPanel();
        instrumentSelector = new JComboBox<>(getInstrumentNames());
        instrumentSelector.setSelectedIndex(DEFAULT_INSTRUMENT);
        instrumentSelector.addActionListener(e -> changeInstrument(instrumentSelector.getSelectedIndex()));
        topPanel.add(new JLabel("Instrument:"));
        topPanel.add(instrumentSelector);

        volumeSlider = new JSlider(0, 127, 80); // MIDI volume range (0-127)
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        topPanel.add(new JLabel("Volume:"));
        topPanel.add(volumeSlider);


        // Tuşları içeren panel
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new GridLayout(1, NOTE_NAMES.length));

        for (int i = 0; i < NOTE_NAMES.length; i++) {
            JButton keyButton = new JButton(NOTE_NAMES[i]);
            int note = NOTE_KEYS[i];
            keyButton.setBackground(isSharpNote(NOTE_NAMES[i]) ? Color.BLACK : Color.WHITE);
            keyButton.setForeground(isSharpNote(NOTE_NAMES[i]) ? Color.WHITE : Color.BLACK);

            keyButton.addActionListener(e -> playNoteThreaded(note));

            keyPanel.add(keyButton);
        }

        add(topPanel, BorderLayout.NORTH);
        add(keyPanel, BorderLayout.CENTER);

        // Varsayılan enstrümanı ayarla
        changeInstrument(DEFAULT_INSTRUMENT);
    }

    private void playNoteThreaded(int note) {
        new Thread(() -> playNote(note)).start();
    }


    private void playNote(int note) {
        int volume = volumeSlider.getValue(); // Mevcut slider değerini al
        channel.noteOn(note, volume); // Volume değeri artık dinamik
        try {
            Thread.sleep(1000); // 1 saniye çal
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.noteOff(note);
    }


    private boolean isSharpNote(String note) {
        return note.contains("#");
    }

    private String[] getInstrumentNames() {
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        String[] names = new String[instruments.length];
        for (int i = 0; i < instruments.length; i++) {
            names[i] = instruments[i].getName();
        }
        return names;
    }

    private void changeInstrument(int instrumentIndex) {
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        if (instrumentIndex >= 0 && instrumentIndex < instruments.length) {
            synthesizer.loadInstrument(instruments[instrumentIndex]);
            channel.programChange(instrumentIndex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Piano Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PianoPanel());
            frame.setSize(800, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
