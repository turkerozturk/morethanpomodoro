package org.example.jpanels.pomodoro;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.sounds.AsyncBeep;

import javax.swing.*;


public class EndingSoundPanel extends JPanel {

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
            playFrequencyBeep();
        }
    }

    public void playFrequencyBeep() {
        //  System.out.println("Sinüs dalgası çalıyor...");
        AsyncBeep.generateToneAsync(1000, 2000, false, endingSoundVolume);
    }

    public void playFrequencyBeepIfSelected() {
        if (endingSoundToggleButton.isSelected()) {
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

}
