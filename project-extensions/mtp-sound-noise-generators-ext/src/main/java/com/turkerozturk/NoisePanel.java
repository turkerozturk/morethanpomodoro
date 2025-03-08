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

import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoisePanel extends JPanel implements SoundController, PanelPlugin {

    private JComboBox<NoiseType> noiseTypeComboBox;
    private JToggleButton playStopButton;
    private JSlider volumeSlider;
    private NoiseGenerator noiseGenerator;
    private AnotherNoiseGenerator anotherNoiseGenerator;


    private NoiseType noiseType;

    public JPanel getPlayerPanel() {
        return this;
    }

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();

    public NoisePanel() {
        setLayout(new BorderLayout());
        int initialVolume = Integer.parseInt(props.getProperty("noise.generator.volume"));

        //noiseTypeComboBox = new JComboBox<>(new String[]{"White Noise", "Brown Noise", "Pink Noise", "Another Noise"});
        noiseTypeComboBox = new JComboBox<>(NoiseType.values());
        playStopButton = new JToggleButton("Play");
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, initialVolume);

        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBorder(
                BorderFactory.createTitledBorder(
                        bundle.getString("slider.noise.generator.loudness")));
        volumeSlider.setValue(initialVolume);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Noise Type:"));
        controlPanel.add(noiseTypeComboBox);
        controlPanel.add(playStopButton);
        controlPanel.add(new JLabel("Volume:"));
        controlPanel.add(volumeSlider);

        add(controlPanel, BorderLayout.NORTH);

        noiseGenerator = new NoiseGenerator();
        //noiseGenerator.setVolume(volumeSlider.getValue() / 100.0f);
        noiseGenerator.setVolume(volumeSlider.getValue());

        anotherNoiseGenerator = new AnotherNoiseGenerator();
        anotherNoiseGenerator.setVolume(volumeSlider.getValue());
        anotherNoiseGenerator.stop(); // setVolume icinde play var cunku. acilista calmamasi icin.

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



        volumeSlider.addChangeListener(e -> {
            int volumeValue = volumeSlider.getValue();
            noiseGenerator.setVolume(volumeValue);
            anotherNoiseGenerator.setVolume(volumeValue);
        });




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

    @Override
    public String getTabName() {
        return "plugin.sound.noise.generators.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.PRODUCTIVITY;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
