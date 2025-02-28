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

import com.turkerozturk.BinauralBeatsGenerator;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class BinauralPanel extends JPanel implements SoundController, PanelPlugin{


    private BinauralBeatsGenerator binauralBeatsGenerator;
    private JSlider sliderBinauralBeatSlider;
    private JSpinner spinnerBinauralBaseFrequency;
    private JSpinner spinnerBinauralBeatFrequency;
    private JToggleButton playToggleButton;
    private boolean isBinauralBeatsPlaying;
    private int binauralBaseFrequency, binauralBeatFrequency, binauralBeatsVolume;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    public BinauralPanel() {

        loadVariablesFromConfig();

        initializeBinauralBeatsGenerator();

        preparePlayToggleButton();
        playToggleButton.addActionListener(e -> toggleBinauralBeats());

        prepareVolumeSlider();
        sliderBinauralBeatSlider.addChangeListener(e -> changeBinauralSoundVolume());

        prepareBaseFrequencySpinner();
        spinnerBinauralBaseFrequency.addChangeListener(e -> changeBinauralBaseFrequency());

        prepareBeatFrequencySpinner();
        spinnerBinauralBeatFrequency.addChangeListener(e -> changeBinauralBeatFrequency());

    }

    private void loadVariablesFromConfig() {
        isBinauralBeatsPlaying = Integer.parseInt(
                props.getProperty("binaural.beats.is.playing", "0")) == 1;

        binauralBeatsVolume = Integer.parseInt(
                props.getProperty("binaural.beats.sound.volume", "25"));

        binauralBaseFrequency = Integer.parseInt(props.getProperty("binaural.beats.base.frequency"
                , "440"));

        binauralBeatFrequency = Integer.parseInt(props.getProperty("binaural.beats.beat.frequency"
                , "5"));
    }

    private void initializeBinauralBeatsGenerator() {
        binauralBeatsGenerator = new BinauralBeatsGenerator(binauralBaseFrequency,
                binauralBeatFrequency, binauralBeatsVolume);
    }

    private void preparePlayToggleButton() {

        playToggleButton = new JToggleButton(translate("button.binaural.beats.deactivate"));

        this.add(playToggleButton);

        processBinauralBeats();

    }

    private void prepareVolumeSlider() {

        sliderBinauralBeatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, binauralBeatsVolume);

        sliderBinauralBeatSlider.setMajorTickSpacing(10);
        sliderBinauralBeatSlider.setMinorTickSpacing(1);
        sliderBinauralBeatSlider.setPaintTicks(true);
        sliderBinauralBeatSlider.setPaintLabels(true);
        sliderBinauralBeatSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.binaural.beats.loudness")));
        this.add(sliderBinauralBeatSlider);

    }

    private void prepareBaseFrequencySpinner() {

        SpinnerModel spinnerModel6 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBaseFrequency = new JSpinner(spinnerModel6);
        spinnerBinauralBaseFrequency.setValue((int) binauralBaseFrequency);
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel3 = new JPanel();
        panel3.add(new JLabel(bundle.getString("spinner.binaural.beats.base.frequency")));
        panel3.add(spinnerBinauralBaseFrequency);
        this.add(panel3);

    }

    private void prepareBeatFrequencySpinner() {

        SpinnerModel spinnerModel7 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBeatFrequency = new JSpinner(spinnerModel7);
        spinnerBinauralBeatFrequency.setValue((int) binauralBeatFrequency);
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel4 = new JPanel();
        panel4.add(new JLabel(bundle.getString("spinner.binaural.beats.beat.frequency")));
        panel4.add(spinnerBinauralBeatFrequency);
        this.add(panel4);

    }


    private void changeBinauralBeatFrequency() {
        binauralBeatFrequency = (int) spinnerBinauralBeatFrequency.getValue();
        binauralBeatsGenerator.setBeatFrequency(binauralBeatFrequency);
    }


    private void changeBinauralBaseFrequency() {
        binauralBaseFrequency = (int) spinnerBinauralBaseFrequency.getValue();
        binauralBeatsGenerator.setBaseFrequency(binauralBaseFrequency);
    }

    private void changeBinauralSoundVolume() {
        binauralBeatsVolume = (int) (sliderBinauralBeatSlider.getValue());
        binauralBeatsGenerator.setVolume((int) (sliderBinauralBeatSlider.getValue()));

    }

    private void toggleBinauralBeats() {
        isBinauralBeatsPlaying = !isBinauralBeatsPlaying;
        processBinauralBeats();
    }

    private void processBinauralBeats() {
        if (isBinauralBeatsPlaying) {
            playToggleButton.setText(translate("button.binaural.beats.deactivate"));
            binauralBeatsGenerator.start();
        } else {
            playToggleButton.setText(translate("button.binaural.beats.activate"));
            binauralBeatsGenerator.stop();
        }
    }

    public String translate(String key) {
        return bundle.getString(key);
    }


    @Override
    public void mute() {
        if (isBinauralBeatsPlaying) {
            playToggleButton.setText(translate("button.binaural.beats.activate"));
            binauralBeatsGenerator.stop();
        }
    }

    @Override
    public void unmute() {
        if (isBinauralBeatsPlaying) {
            playToggleButton.setText(translate("button.binaural.beats.deactivate"));
            binauralBeatsGenerator.start();
        }
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public String getTabName() {
        return "plugin.sound.binaural.beats.title";
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
