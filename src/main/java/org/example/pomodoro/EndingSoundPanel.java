package org.example.pomodoro;

import org.example.sounds.AsyncBeep;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class EndingSoundPanel extends JPanel {

    JToggleButton toggleFinishSoundButton, isEndingSoundMutedButton;


    JSlider endingSoundVolumeSlider;
    int wavSoundVolume, frequencySoundVolume;

    private Properties props = new Properties();

    private String language;
    private String country;

    private ResourceBundle bundle;
    public EndingSoundPanel() {

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





        toggleFinishSoundButton = new JToggleButton(translate("button.ending.sound.initial"));
        toggleFinishSoundButton.addActionListener(e -> toggleFinishSound());
        toggleFinishSoundButton.setSelected(true);


        endingSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, frequencySoundVolume);
        endingSoundVolumeSlider.setMajorTickSpacing(10);
        endingSoundVolumeSlider.setMinorTickSpacing(1);
        endingSoundVolumeSlider.setPaintTicks(true);
        endingSoundVolumeSlider.setPaintLabels(true);
        //Font font = new Font("Serif", Font.ITALIC, 15);
        //endingSoundVolumeSlider.setFont(font);
        endingSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.ending.sound.loudness")));

        isEndingSoundMutedButton = new JToggleButton(translate("button.ending.sound.mute"));


        this.add(isEndingSoundMutedButton);
        this.add(endingSoundVolumeSlider);
        this.add(toggleFinishSoundButton);

        endingSoundVolumeSlider.addChangeListener(e -> changeEndingSoundVolume());


    }

    public void changeEndingSoundVolume() {
        if (!endingSoundVolumeSlider.getValueIsAdjusting()) {
            frequencySoundVolume = endingSoundVolumeSlider.getValue();
            playFrequencyBeep();
        }
    }

    public void playFrequencyBeep() {
        //  System.out.println("Sinüs dalgası çalıyor...");
        // System.out.println("PomodoroFrame frequencySoundVolume: " + frequencySoundVolume);
        AsyncBeep.generateToneAsync(1000, 2000, false, frequencySoundVolume);
        // System.out.println("Kare dalga çalıyor...");
        //AsyncBeep.generateToneAsync(1000, 6000, true, frequencySoundVolume);

    }

    public void playFrequencyBeepIfSelected() {
        if (toggleFinishSoundButton.isSelected()) {
            playFrequencyBeep();
        }
    }



        private void toggleFinishSound() {


        if (toggleFinishSoundButton.isSelected()) {
            toggleFinishSoundButton.setText(translate("timer.end.sound.on"));
        } else {
            toggleFinishSoundButton.setText(translate("timer.end.sound.off"));
        }


    }



    public String translate(String key) {
        return bundle.getString(key);
    }

}
