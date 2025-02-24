package org.example.jpanels.pomodoro.subpanels;

import org.example.MetronomePlayerWav;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class TickSoundWavPanel extends JPanel implements SoundController {

    private final MetronomePlayerWav metronomePlayer;
    private int metronomeInterval; // saniye
    private String soundType;
    private String soundFile;
    //private int wavSoundVolume;

    private int tickSoundVolume;


    JSlider tickSoundVolumeSlider;
    private final JToggleButton  toggleRandomTickButton;
    private final JToggleButton toggleTickSoundButton;


    private boolean isRandomTick;
    private boolean isMuted;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();


    public TickSoundWavPanel() {


        loadConfigVariables();



        // Metronom ayarla
        metronomePlayer = new MetronomePlayerWav(metronomeInterval, soundType, soundFile, isRandomTick);
        metronomePlayer.setRandomEnabled(isRandomTick);


        //metronomePlayer.setWavSoundVolume(wavSoundVolume);
        metronomePlayer.setVolume(tickSoundVolume);









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
            soundFile = props.getProperty("pomodoro.tick.sound.wav.file");
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
