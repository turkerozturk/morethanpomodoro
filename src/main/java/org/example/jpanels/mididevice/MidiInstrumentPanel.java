package org.example.jpanels.mididevice;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MidiInstrumentPanel extends JPanel {
    private JComboBox<String> deviceComboBox;
    private JComboBox<String> instrumentComboBox;
    private JButton playButton;
    private Synthesizer synthesizer;
    private MidiChannel channel;

    public MidiInstrumentPanel() {
        setLayout(new BorderLayout());

        deviceComboBox = new JComboBox<>();
        instrumentComboBox = new JComboBox<>(getInstrumentNames());
        playButton = new JButton("Play Note");

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("MIDI Device:"));
        controlPanel.add(deviceComboBox);
        controlPanel.add(new JLabel("Instrument:"));
        controlPanel.add(instrumentComboBox);
        controlPanel.add(playButton);

        add(controlPanel, BorderLayout.NORTH);

        loadMidiDevices();

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playNote();
            }
        });
    }

    private void loadMidiDevices() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            deviceComboBox.addItem(info.getName());
        }

        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private String[] getInstrumentNames() {
        String[] instruments = new String[128];
        for (int i = 0; i < 128; i++) {
            instruments[i] = "Program " + i;
        }
        return instruments;
    }

    private void playNote() {
        int instrument = instrumentComboBox.getSelectedIndex();
        if (channel != null) {
            channel.programChange(instrument);
            channel.noteOn(60, 100); // Middle C

            new Timer(1000, e -> channel.noteOff(60)).start();
        }
    }
}
