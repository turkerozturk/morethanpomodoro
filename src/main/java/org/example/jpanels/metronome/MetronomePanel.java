package org.example.jpanels.metronome;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MetronomePanel extends JPanel implements SoundController {
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

    public JPanel getPlayerPanel() {
        return this;
    }

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

    private void prepareBPMSpinner(){
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


    @Override
    public void mute() {
        isMetronomeEnabled = false;
        startStopButton.setText(translate("button.metronome.sound.activate"));
        stopMetronome();
    }

    @Override
    public void unmute() {
        isMetronomeEnabled = true;
        startStopButton.setText(translate("button.metronome.sound.deactivate"));

        startMetronome();
    }

    @Override
    public boolean isMuted() {
        return false;
    }
}
