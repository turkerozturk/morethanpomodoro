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

import com.turkerozturk.MetronomePlayerMidi;
import com.turkerozturk.MetronomePlayerWav;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class TickSoundMidiPanel extends JPanel implements SoundController {

    private final MetronomePlayerMidi metronomePlayer;
    private int metronomeInterval; // saniye
    private String soundType;
    private String soundFile;
    private int wavSoundVolume;

    private int tickSoundVolume;

    private JSpinner spinnerSelectMidiInstrument;
    private JSpinner spinnerSelectMidiNote;
    JSlider tickSoundVolumeSlider;
    private final JToggleButton  toggleRandomTickButton;
    private final JToggleButton toggleTickSoundButton;
    private int midiInstrument;
    private int midiNote;
    private int midiVolume;
    private boolean isRandomTick;
    private boolean isMuted;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();


    public TickSoundMidiPanel() {


        loadConfigVariables();



        // Metronom ayarla
        metronomePlayer = new MetronomePlayerMidi(metronomeInterval, soundType, soundFile, isRandomTick);
        metronomePlayer.setRandomEnabled(isRandomTick);
        //metronomePlayer.setMidiVolume(midiVolume);
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument,midiNote);
        //metronomePlayer.setWavSoundVolume(wavSoundVolume);
        metronomePlayer.setVolume(tickSoundVolume);









        // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SliderDemoProject/src/components/SliderDemo.java
        tickSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, midiVolume);
        tickSoundVolumeSlider.setMajorTickSpacing(10);
        tickSoundVolumeSlider.setMinorTickSpacing(1);
        tickSoundVolumeSlider.setPaintTicks(true);
        tickSoundVolumeSlider.setPaintLabels(true);
        tickSoundVolumeSlider.addChangeListener(e -> changeTickSoundVolume());
        tickSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.tick.sound.loudness")));
        this.add(tickSoundVolumeSlider);



        /*
        SpinnerModel spinnerModel1 = new SpinnerNumberModel(0, 0, 100, 1);
        spinnerSelectMidiInstrument = new JSpinner(spinnerModel1);
        spinnerSelectMidiInstrument.setValue((int) midiInstrument);
        spinnerSelectMidiInstrument.setPreferredSize(new Dimension(100, 40)); // Genişlik 80, yükseklik 25
        spinnerSelectMidiInstrument.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("spinner.tick.sound.instrument")));
        spinnerSelectMidiInstrument.addChangeListener(e -> changeTickSoundMidiInstrument());
        this.add(spinnerSelectMidiInstrument);

        SpinnerModel spinnerModel2 = new SpinnerNumberModel(0, 0, 100, 1);
        spinnerSelectMidiNote = new JSpinner(spinnerModel2);
        spinnerSelectMidiNote.setValue((int) midiNote);
        spinnerSelectMidiNote.setPreferredSize(new Dimension(100, 40)); // Genişlik 80, yükseklik 25
        spinnerSelectMidiNote.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("spinner.tick.sound.note")));
        spinnerSelectMidiNote.addChangeListener(e -> changeTickSoundMidiNote());
        this.add(spinnerSelectMidiNote);
        */

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
            soundFile = props.getProperty("pomodoro.tick.sound.wav.file");
            wavSoundVolume = Integer.parseInt(props.getProperty("pomodoro.tick.sound.wav.volume"));
            tickSoundVolume = wavSoundVolume;
        } else if(soundType.equals("MIDI")) {
            midiInstrument = Integer.parseInt(props.getProperty("pomodoro.tick.sound.midi.instrument")); // default: 9 (Percussion)
            midiNote = Integer.parseInt(props.getProperty("pomodoro.tick.sound.midi.note")); // default: 60 (Middle C)
            midiVolume = Integer.parseInt(props.getProperty("pomodoro.tick.sound.midi.volume")); // default volume level: 100
            tickSoundVolume = midiVolume;
        } else {
            // beep
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


    private void changeTickSoundMidiNote() {
        int value = (Integer) spinnerSelectMidiNote.getValue();
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument, value);

    }

    public void changeTickSoundVolume() {
        if (!tickSoundVolumeSlider.getValueIsAdjusting()) {
            tickSoundVolume = tickSoundVolumeSlider.getValue();
            metronomePlayer.setVolume(tickSoundVolume);
            metronomePlayer.tick();
        }
    }

    private void changeTickSoundMidiInstrument() {
        int value = (Integer) spinnerSelectMidiInstrument.getValue();
        metronomePlayer.setMidiInstrumentAndNote(value, midiNote);

        //label.setText("Seçilen Değer: " + value);
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
