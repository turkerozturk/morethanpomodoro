package org.example.jpanels.noisegenerator;

import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoisePanel extends JPanel implements SoundController {
    private JComboBox<String> noiseTypeComboBox;
    private JToggleButton playStopButton;
    private JSlider volumeSlider;
    private NoiseGenerator noiseGenerator;

    private String noiseType;

    public JPanel getPlayerPanel() {
        return this;
    }

    public NoisePanel() {
        setLayout(new BorderLayout());

        noiseTypeComboBox = new JComboBox<>(new String[]{"White Noise", "Brown Noise", "Pink Noise"});
        playStopButton = new JToggleButton("Play");
        volumeSlider = new JSlider(0, 100, 50);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Noise Type:"));
        controlPanel.add(noiseTypeComboBox);
        controlPanel.add(playStopButton);
        controlPanel.add(new JLabel("Volume:"));
        controlPanel.add(volumeSlider);

        add(controlPanel, BorderLayout.NORTH);

        noiseGenerator = new NoiseGenerator();

        playStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playStopButton.isSelected()) {
                    playNoise();
                } else {
                    stopNoise();
                }
            }
        });

        volumeSlider.addChangeListener(e -> noiseGenerator.setVolume(volumeSlider.getValue() / 100.0f));

        noiseTypeComboBox.addActionListener(e -> changeNoise());
    }

    private void changeNoise() {
        stopNoise();
        playNoise();
    }

    private void playNoise() {
        noiseType = (String) noiseTypeComboBox.getSelectedItem();
        noiseGenerator.start(noiseType);
        playStopButton.setText("Stop");
    }

    private void stopNoise() {
        noiseGenerator.stop();
        playStopButton.setText("Play");
    }

    @Override
    public void mute() {
        if (playStopButton.isSelected()) {
            stopNoise();
        }
    }

    @Override
    public void unmute() {
        if (playStopButton.isSelected()) {
            playNoise();
        }
    }

    @Override
    public boolean isMuted() {
        return false;
    }
}
