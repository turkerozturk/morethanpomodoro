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
package com.turkerozturk.jpanels.pomodoro.subpanels;

import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;
import com.turkerozturk.sounds.AsyncBeep;

import javax.swing.*;


public class EndingSoundPanel extends JPanel implements SoundController {

    private JToggleButton endingSoundToggleButton;
    private JSlider endingSoundVolumeSlider;
    private boolean isEndingSoundEnabled;
    private int endingSoundVolume;
    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();

    public EndingSoundPanel() {

        loadVariablesFromConfig();

        initializeEndingSound();

        preparePlayToggleButton();
        endingSoundToggleButton.addActionListener(e -> toggleEndingSound());

        prepareVolumeSlider();
        endingSoundVolumeSlider.addChangeListener(e -> changeEndingSoundVolume());

    }

    private void loadVariablesFromConfig() {
        isEndingSoundEnabled = Integer.parseInt(
                props.getProperty("pomodoro.ending.sound.is.enabled", "1")) == 1;

        endingSoundVolume = Integer.parseInt(
                props.getProperty("pomodoro.ending.sound.volume", "45"));
    }

    private void initializeEndingSound() {
            // intentionally empty
    }

    private void prepareVolumeSlider() {
        endingSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, endingSoundVolume);
        endingSoundVolumeSlider.setMajorTickSpacing(10);
        endingSoundVolumeSlider.setMinorTickSpacing(1);
        endingSoundVolumeSlider.setPaintTicks(true);
        endingSoundVolumeSlider.setPaintLabels(true);

        endingSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.ending.sound.volume")));
        this.add(endingSoundVolumeSlider);
    }

    private void preparePlayToggleButton() {
        endingSoundToggleButton = new JToggleButton();
        this.add(endingSoundToggleButton);

        processEndingSound();

    }

    public void changeEndingSoundVolume() {
        if (!endingSoundVolumeSlider.getValueIsAdjusting()) {
            endingSoundVolume = endingSoundVolumeSlider.getValue();
            if(isEndingSoundEnabled) {
                playFrequencyBeep();
            }
        }
    }

    public void playFrequencyBeep() {
        //  System.out.println("Sinüs dalgası çalıyor...");
        AsyncBeep.generateToneAsync(1000, 2000, false, endingSoundVolume);
    }

    public void playFrequencyBeepIfSelected() {
        if (isEndingSoundEnabled) {
            playFrequencyBeep();
        }
    }

    private void toggleEndingSound() {
        isEndingSoundEnabled = !isEndingSoundEnabled;
        processEndingSound();
    }

    public void processEndingSound() {
        endingSoundToggleButton.setSelected(isEndingSoundEnabled);

        if (isEndingSoundEnabled) {
            endingSoundToggleButton.setText(translate("button.ending.sound.deactivate"));
        } else {
            endingSoundToggleButton.setText(translate("button.ending.sound.activate"));
        }

    }

    public String translate(String key) {
        return bundle.getString(key);
    }

    @Override
    public void mute() {
            isEndingSoundEnabled = false;
            endingSoundToggleButton.setText(translate("button.ending.sound.activate"));


    }

    @Override
    public void unmute() {
            isEndingSoundEnabled = true;
            endingSoundToggleButton.setText(translate("button.ending.sound.deactivate"));



    }

    @Override
    public boolean isMuted() {
        return false;
    }
}
