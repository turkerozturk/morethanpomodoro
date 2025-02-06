package org.example;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoisePanel extends JPanel {
    private JComboBox<String> noiseTypeComboBox;
    private JToggleButton playStopButton;
    private JSlider volumeSlider;
    private NoiseGenerator noiseGenerator;

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
    }

    private void playNoise() {
        String noiseType = (String) noiseTypeComboBox.getSelectedItem();
        noiseGenerator.start(noiseType);
        playStopButton.setText("Stop");
    }

    private void stopNoise() {
        noiseGenerator.stop();
        playStopButton.setText("Play");
    }
}
