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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestSpeakersPanel extends JPanel implements PanelPlugin{
    private JComboBox<Mixer.Info> outputDeviceComboBox;
    private JButton leftButton;
    private JButton rightButton;

    public TestSpeakersPanel() {
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

    @Override
    public String getTabName() {
        return "plugin.test.speakers.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}

