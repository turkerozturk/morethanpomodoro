package org.example.jpanels.noisegenerator;

import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoisePanel extends JPanel implements SoundController {

    private JComboBox<NoiseType> noiseTypeComboBox;
    private JToggleButton playStopButton;
    private JSlider volumeSlider;
    private NoiseGenerator noiseGenerator;
    private AnotherNoiseGenerator anotherNoiseGenerator;


    private NoiseType noiseType;

    public JPanel getPlayerPanel() {
        return this;
    }

    public NoisePanel() {
        setLayout(new BorderLayout());

        //noiseTypeComboBox = new JComboBox<>(new String[]{"White Noise", "Brown Noise", "Pink Noise", "Another Noise"});
        noiseTypeComboBox = new JComboBox<>(NoiseType.values());
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
        anotherNoiseGenerator = new AnotherNoiseGenerator();

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

        volumeSlider.addChangeListener(e ->
                noiseGenerator.setVolume(volumeSlider.getValue() / 100.0f

        ));

        noiseTypeComboBox.addActionListener(e -> changeNoise());
    }

    private void changeNoise() {
        if(playStopButton.isSelected()) {
            stopNoise();
            playNoise();
        }
    }

    private void playNoise() {
        noiseType = (NoiseType) noiseTypeComboBox.getSelectedItem();
        if(noiseType.equals(NoiseType.ANOTHER_NOISE)) {
            anotherNoiseGenerator.play();
        } else {
            noiseGenerator.start(noiseType.toString());
        }
        playStopButton.setText("Stop");
    }

    private void stopNoise() {


        anotherNoiseGenerator.stop();

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
