package org.example.jpanels.binaural;

import org.example.BinauralBeatsGenerator;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class BinauralPanel extends JPanel implements SoundController {


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
}
