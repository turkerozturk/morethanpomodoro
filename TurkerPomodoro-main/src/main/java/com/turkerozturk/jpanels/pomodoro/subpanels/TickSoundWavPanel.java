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

import com.turkerozturk.MetronomePlayerWav;
import com.turkerozturk.comboboxes.WavFile;
import com.turkerozturk.comboboxes.WavFileComboBox;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class TickSoundWavPanel extends JPanel implements SoundController {

    private final MetronomePlayerWav metronomePlayer;
    private int metronomeInterval; // saniye
    private String soundType;
    private String soundFileName;
    //private int wavSoundVolume;

    private int tickSoundVolume;


    JSlider tickSoundVolumeSlider;
    private final JToggleButton  toggleRandomTickButton;
    private final JToggleButton toggleTickSoundButton;

    //JLabel jLabel;


    private boolean isRandomTick;
    private boolean isMuted;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();


    public TickSoundWavPanel() {


        loadConfigVariables();



        // Metronom ayarla
        metronomePlayer = new MetronomePlayerWav(metronomeInterval, soundType, soundFileName);
        metronomePlayer.setRandomEnabled(isRandomTick);


        //metronomePlayer.setWavSoundVolume(wavSoundVolume);
        metronomePlayer.setVolume(tickSoundVolume);





        //jLabel = new JLabel("tick inverval: " + metronomeInterval + "");
        //this.add(jLabel);


        JPanel panelForComboBox = new JPanel();
        panelForComboBox.setLayout(new BoxLayout(panelForComboBox, BoxLayout.X_AXIS));
        WavFileComboBox wavFileComboBox = new WavFileComboBox();
        wavFileComboBox.populateComboBox();
        panelForComboBox.add(wavFileComboBox);
        this.add(panelForComboBox);

        JButton btnGetSelection = new JButton("Select");
        btnGetSelection.addActionListener(e -> {
            WavFile wavFile = wavFileComboBox.getSelectedWavFile();
            // İstediğiniz işlemi yapın. Örneğin label'ı güncelleyin:
            //jLabel.setText("Seçilen Dosya: " + fileName);
            metronomePlayer.setWavFile(wavFile.getFileName());
            metronomePlayer.setTickInterval(wavFile.getDurationSec());
            //System.out.println(wavFile.getFileName() + " interval: " + wavFile.getDurationSec());
        });
        this.add(btnGetSelection);


        wavFileComboBox.addActionListener(e -> {
            WavFile wavFile = wavFileComboBox.getSelectedWavFile();
            // İstediğiniz işlemi yapın. Örneğin label'ı güncelleyin:
            //jLabel.setText("Seçilen Dosya: " + fileName);
            metronomePlayer.setWavFile(wavFile.getFileName());
            metronomePlayer.setTickInterval(wavFile.getDurationSec());
            //System.out.println(wavFile.getFileName() + " interval: " + wavFile.getDurationSec());
        });



        // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SliderDemoProject/src/components/SliderDemo.java
        tickSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, tickSoundVolume);
        tickSoundVolumeSlider.setMajorTickSpacing(10);
        tickSoundVolumeSlider.setMinorTickSpacing(1);
        tickSoundVolumeSlider.setPaintTicks(true);
        tickSoundVolumeSlider.setPaintLabels(true);
        tickSoundVolumeSlider.addChangeListener(e -> changeTickSoundVolume());
        tickSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.tick.sound.loudness")));
        this.add(tickSoundVolumeSlider);





        toggleTickSoundButton = new JToggleButton(translate("sound.tick.mute.unmute"));
        muteee();
        toggleTickSoundButton.addActionListener(e -> muteUnmute());

        toggleRandomTickButton = new JToggleButton(translate("random.tick.on"));
        toggleRandomTickButton.setSelected(isRandomTick);
        if (toggleRandomTickButton.isSelected()) {
            toggleRandomTickButton.setText(translate("random.tick.on"));
        } else {
            toggleRandomTickButton.setText(translate("random.tick.off"));
        }
        toggleRandomTickButton.addActionListener(e -> toggleRandomTick());

        this.add(toggleTickSoundButton);
        this.add(toggleRandomTickButton);


    }

    private void loadConfigVariables() {

        isMuted = Integer.parseInt(props.getProperty("pomodoro.tick.sound.is.muted")) == 1;

        isRandomTick = Integer.parseInt(props.getProperty("pomodoro.tick.sound.random.is.enabled")) == 1;

        metronomeInterval = Integer.parseInt(props.getProperty("pomodoro.timer.metronome.interval"));

        soundType = props.getProperty("pomodoro.tick.sound.type"); // WAV or MIDI or BEEP
        if(soundType.equals("WAV")) {
            soundFileName = props.getProperty("pomodoro.tick.sound.wav.file");
            tickSoundVolume = Integer.parseInt(props.getProperty("pomodoro.tick.sound.wav.volume"));
            //tickSoundVolume = wavSoundVolume;
        }



    }


    private void muteUnmute() {
        isMuted = !isMuted;
        muteee();
    }

    private void muteee() {
        metronomePlayer.setMuted(isMuted);
        toggleTickSoundButton.setSelected(isMuted);
        toggleTickSoundButton.setText(isMuted ? translate("mute.ticks") : translate("unmute.ticks"));
    }





    public void changeTickSoundVolume() {
        if (!tickSoundVolumeSlider.getValueIsAdjusting()) {
            tickSoundVolume = tickSoundVolumeSlider.getValue();
            metronomePlayer.setVolume(tickSoundVolume);
            metronomePlayer.tick();
        }
    }




    private void toggleRandomTick() {

        if (toggleRandomTickButton.isSelected()) {
            metronomePlayer.setRandomEnabled(true);
            toggleRandomTickButton.setText(translate("random.tick.on"));
        } else {
            metronomePlayer.setRandomEnabled(false);
            toggleRandomTickButton.setText(translate("random.tick.off"));
        }
    }

    public void tick() {
        metronomePlayer.tick();

    }


    public String translate(String key) {
        return bundle.getString(key);
    }


    @Override
    public void mute() {
        muteUnmute();
    }

    @Override
    public void unmute() {
        muteUnmute();
    }

    @Override
    public boolean isMuted() {
        return false;
    }
}
