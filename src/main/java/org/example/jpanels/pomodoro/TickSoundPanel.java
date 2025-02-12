package org.example.jpanels.pomodoro;

import org.example.MetronomePlayer;
import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class TickSoundPanel extends JPanel implements SoundController {

    private final MetronomePlayer metronomePlayer;
    private int metronomeInterval; // saniye
    private String soundType;
    private String soundFile;

    int wavSoundVolume;


    JSpinner spinnerSelectMidiInstrument, spinnerSelectMidiNote;

    JSlider tickSoundVolumeSlider;

    JToggleButton  toggleRandomTickButton, toggleTickSoundButton;

    int midiInstrument, midiNote, midiVolume;

    private boolean isRandomTick;



    private boolean isMuted = false;

    private Properties props = new Properties();

    private String language;
    private String country;

    private ResourceBundle bundle;



    public TickSoundPanel() {

        language = props.getProperty("language.locale", "en");
        country = props.getProperty("language.country", "EN");

        InputStream is = getClass().getResourceAsStream("/config.properties");
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Locale locale = new Locale(language, country);
        bundle = ResourceBundle.getBundle("messages", locale);


        // = 9; // percussion, drums, bells etc.
        midiInstrument = Integer.parseInt(props.getProperty("sound.midi.instrument", "9")); // default: 9 (Percussion)
        midiNote = Integer.parseInt(props.getProperty("sound.midi.note", "60")); // default: 60 (Middle C)
        midiVolume = Integer.parseInt(props.getProperty("sound.midi.volume", "100")); // default volume level: 100

        metronomeInterval = Integer.parseInt(props.getProperty("metronome.interval", "1"));
        soundType = props.getProperty("sound.type", "WAV");
        soundFile = props.getProperty("sound.file", "beep.wav");

        int randomTickAsInt = Integer.parseInt(props.getProperty("pomodoro.sound.random.tick.toggle", "1"));
        isRandomTick = (randomTickAsInt == 1);

        // Metronom ayarla
        metronomePlayer = new MetronomePlayer(metronomeInterval, soundType, soundFile, true);
        metronomePlayer.setRandomEnabled(isRandomTick);
        metronomePlayer.setMidiVolume(midiVolume);
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument,midiNote);
        metronomePlayer.setWavSoundVolume(wavSoundVolume);










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

        toggleTickSoundButton = new JToggleButton(translate("sound.tick.mute.unmute"));
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


    private void muteUnmute() {
        isMuted = !isMuted;
        metronomePlayer.setMuted(isMuted);
        // todo
        toggleTickSoundButton.setText(isMuted ? translate("mute.ticks") : translate("unmute.ticks"));
    }


    private void changeTickSoundMidiNote() {
        int value = (Integer) spinnerSelectMidiNote.getValue();
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument, value);

    }

    public void changeTickSoundVolume() {
        if (!tickSoundVolumeSlider.getValueIsAdjusting()) {
            midiVolume = tickSoundVolumeSlider.getValue();
            metronomePlayer.setMidiVolume(midiVolume);
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
