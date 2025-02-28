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

import javax.swing.*;
import java.awt.*;

public class MetronomePanel extends JPanel implements PanelPlugin {
    //public class MetronomePanel extends JPanel implements PanelPlugin, SoundController {

        private JSpinner bpmSpinner;
    private JToggleButton startStopButton;
    private JSlider volumeSlider;
    private Metronome metronome;

    JPanel panel1;

    private boolean isMetronomeEnabled;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();
    private int metronomeSoundVolume;
    private int bpm;




    public MetronomePanel() {

        loadVariablesFromConfig();

        initializeMetronomePanel();

        // setLayout(new BorderLayout());

        prepareStartStopButton();
        startStopButton.addActionListener(e -> toggleMetronomeSound());

        prepareBPMSpinner();
        bpmSpinner.addChangeListener(e -> changeBPM());


        prepareVolumeSlider();
        volumeSlider.addChangeListener(e -> changeVolume());


    }

    private void toggleMetronomeSound() {
        if (startStopButton.isSelected()) {
            startMetronome();
        } else {
            stopMetronome();
        }
    }

    private void initializeMetronomePanel() {

        panel1 = new JPanel();

        metronome = new Metronome();


    }

    private void prepareVolumeSlider() {


        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, metronomeSoundVolume);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        volumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.metronome.sound.volume")));
        this.add(new JLabel("Volume:"));

        this.add(volumeSlider);
    }

    private void prepareBPMSpinner() {
        bpmSpinner = new JSpinner(new SpinnerNumberModel(bpm, 0, 44100, 1));
        panel1.add(new JLabel("BPM:"));
        panel1.add(bpmSpinner);
        this.add(panel1, BorderLayout.NORTH);


    }

    private void prepareStartStopButton() {
        startStopButton = new JToggleButton("Start");
        this.add(startStopButton);
        processEndingSound();

    }


    public void processEndingSound() {
        startStopButton.setSelected(isMetronomeEnabled);

        if (isMetronomeEnabled) {
            startStopButton.setText(translate("button.metronome.sound.deactivate"));
            startMetronome();

        } else {
            startStopButton.setText(translate("button.metronome.sound.activate"));
            stopMetronome();

        }

    }


    private void loadVariablesFromConfig() {
        isMetronomeEnabled = Integer.parseInt(
                props.getProperty("metronome.sound.is.enabled", "0")) == 1;

        metronomeSoundVolume = Integer.parseInt(
                props.getProperty("metronome.sound.volume", "100"));

        bpm = Integer.parseInt(
                props.getProperty("metronome.sound.bpm", "80"));

    }

    public void changeBPM() {
        bpm = (Integer) bpmSpinner.getValue();
        metronome.setBPM(bpm);
        stopMetronome();
        startMetronome();
    }

    public void changeVolume() {
        metronomeSoundVolume = (int) (volumeSlider.getValue() / 100.0f);
        metronome.setVolume(metronomeSoundVolume);
        stopMetronome();
        startMetronome();
    }

    private void startMetronome() {
        metronome.start((Integer) bpm);
        startStopButton.setText("Stop");
    }

    private void stopMetronome() {
        metronome.stop();
        startStopButton.setText("Start");
    }

    public String translate(String key) {
        return bundle.getString(key);
    }


    private boolean muted = false;
    private float previousVolume = 1.0f;  // 1.0 = %100

    /*
    @Override
    public void mute() {
        if (!muted && metronome.isPlaying()) {
            muted = true;
            previousVolume = metronome.getVolume(); // Mevcut sesi sakla
            metronome.setVolume(0);
        }
    }

    @Override
    public void unmute() {
        if (muted && metronome.isPlaying()) {
            muted = false;
            metronome.setVolume(previousVolume); // Eski sesi geri yükle
        }
    }

    @Override
    public boolean isMuted() {
        return muted;
    }
    */
    @Override
    public String getTabName() {
        return "plugin.metronome.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.SOUND;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MetronomePanel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MetronomePanel());
            frame.setSize(800, 600); // Pencere boyutu
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
