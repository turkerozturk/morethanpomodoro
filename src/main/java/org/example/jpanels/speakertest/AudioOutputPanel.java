package org.example.jpanels.speakertest;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AudioOutputPanel extends JPanel {
    private JComboBox<Mixer.Info> outputDeviceComboBox;
    private JButton leftButton;
    private JButton rightButton;

    public AudioOutputPanel() {
        setLayout(new BorderLayout());

        outputDeviceComboBox = new JComboBox<>(getAudioOutputDevices());
        leftButton = new JButton("Left");
        rightButton = new JButton("Right");

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Output Device:"));
        controlPanel.add(outputDeviceComboBox);
        controlPanel.add(leftButton);
        controlPanel.add(rightButton);

        add(controlPanel, BorderLayout.NORTH);

        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playTestSound(true);
            }
        });

        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playTestSound(false);
            }
        });
    }

    private Mixer.Info[] getAudioOutputDevices() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        return mixers;
    }

    private void playTestSound(boolean left) {
        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[44100 * 2]; // 1 second buffer
                for (int i = 0; i < buffer.length; i += 4) {
                    short sample = (short) (Math.sin(2.0 * Math.PI * 440.0 * i / 44100) * 32767);
                    buffer[i] = left ? (byte) (sample & 0xFF) : 0;  // Left channel
                    buffer[i + 1] = left ? (byte) ((sample >> 8) & 0xFF) : 0;
                    buffer[i + 2] = !left ? (byte) (sample & 0xFF) : 0; // Right channel
                    buffer[i + 3] = !left ? (byte) ((sample >> 8) & 0xFF) : 0;
                }

                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}

