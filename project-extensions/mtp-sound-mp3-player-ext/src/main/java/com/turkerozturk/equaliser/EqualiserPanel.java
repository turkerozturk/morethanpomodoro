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
package com.turkerozturk.equaliser;

import javafx.application.Platform;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EqualiserPanel extends JPanel {
    private List<JSlider> sliders = new ArrayList<>();
    private static final int[] FREQUENCIES = {31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000}; // Hz
    private MediaPlayer mediaPlayer;

    public EqualiserPanel(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        setLayout(new GridLayout(1, FREQUENCIES.length, 5, 5));

        for (int i = 0; i < FREQUENCIES.length; i++) {
            int frequency = FREQUENCIES[i];
            JSlider slider = createSlider(frequency);
            sliders.add(slider);
            add(slider);
        }

        applyEqualizerSettings();
    }

    private JSlider createSlider(int frequency) {
        JSlider slider = new JSlider(JSlider.VERTICAL, -12, 12, 0); // -12dB ile +12dB arası
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(6);
        slider.setMinorTickSpacing(3);
        slider.setToolTipText(frequency + " Hz");

        slider.addChangeListener(e -> applyEqualizerSettings());

        return slider;
    }

    private void applyEqualizerSettings() {

        if (mediaPlayer == null) return;

        Platform.runLater(() -> {
            AudioEqualizer equalizer = mediaPlayer.getAudioEqualizer();

            for (int i = 0; i < sliders.size(); i++) {
                double gain = sliders.get(i).getValue(); // Slider değeri dB olarak
                equalizer.getBands().get(i).setGain(gain);

            }
        });
    }
}

