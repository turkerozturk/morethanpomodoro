package org.example.jpanels.binaural;

import org.example.BinauralBeatsGenerator;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class BinauralPanel extends JPanel {

    private float frequencySoundVolume;
    private int binauralBaseFrequency;
    private BinauralBeatsGenerator binauralBeatsGenerator;

    private JSlider sliderBinauralBeatSlider;

    private JSpinner spinnerBinauralBaseFrequency, spinnerBinauralBeatFrequency;


    private int binauralBeatsVolume;



    private int binauralBeatFrequency;

    private JToggleButton toggleBinauralBeatsButton;

    private boolean isBinauralBeatsEnabled;


    private Properties props = new Properties();

    private String language;
    private String country;

    private ResourceBundle bundle;

    public BinauralPanel() {



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

        int binauralBetasAsInt = Integer.parseInt(props.getProperty("button.binaural.beats.mute", "1"));
        isBinauralBeatsEnabled = (binauralBetasAsInt == 1);




        binauralBeatsVolume = Integer.parseInt(props.getProperty("slider.binaural.beats.loudness"));
        sliderBinauralBeatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, binauralBeatsVolume);
        sliderBinauralBeatSlider.setMajorTickSpacing(10);
        sliderBinauralBeatSlider.setMinorTickSpacing(1);
        sliderBinauralBeatSlider.setPaintTicks(true);
        sliderBinauralBeatSlider.setPaintLabels(true);
        sliderBinauralBeatSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.binaural.beats.loudness")));
        this.add(sliderBinauralBeatSlider);
        sliderBinauralBeatSlider.addChangeListener(e -> changeBinauralSoundVolume());

        toggleBinauralBeatsButton = new JToggleButton(translate("button.binaural.beats.mute"));
        toggleBinauralBeatsButton.addActionListener(e -> toggleBinauralBeats());

        toggleBinauralBeatsButton.setSelected(isBinauralBeatsEnabled);





        this.add(toggleBinauralBeatsButton);

        binauralBaseFrequency = Integer.parseInt(props.getProperty("spinner.binaural.beats.base.frequency"
                , "440"));
        SpinnerModel spinnerModel6 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBaseFrequency = new JSpinner(spinnerModel6);
        spinnerBinauralBaseFrequency.setValue((int) binauralBaseFrequency);
        spinnerBinauralBaseFrequency.addChangeListener(e -> changeBinauralBaseFrequency());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel3 = new JPanel();
        panel3.add(new JLabel(bundle.getString("spinner.binaural.beats.base.frequency")));
        panel3.add(spinnerBinauralBaseFrequency);
        this.add(panel3);

        binauralBeatFrequency = Integer.parseInt(props.getProperty("spinner.binaural.beats.beat.frequency"
                , "5"));
        SpinnerModel spinnerModel7 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBeatFrequency = new JSpinner(spinnerModel7);
        spinnerBinauralBeatFrequency.setValue((int) binauralBeatFrequency);
        spinnerBinauralBeatFrequency.addChangeListener(e -> changeBinauralBeatFrequency());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel4 = new JPanel();
        panel4.add(new JLabel(bundle.getString("spinner.binaural.beats.beat.frequency")));
        panel4.add(spinnerBinauralBeatFrequency);
        this.add(panel4);

        binauralBeatsGenerator = new BinauralBeatsGenerator(binauralBaseFrequency,
                binauralBeatFrequency,frequencySoundVolume);

        //System.out.println(isBinauralBeatsEnabled);
        processBinauralBeats();



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
        isBinauralBeatsEnabled = !isBinauralBeatsEnabled;
        processBinauralBeats();
    }

    private void processBinauralBeats() {
        if (isBinauralBeatsEnabled) {
            toggleBinauralBeatsButton.setText(translate("button.binaural.beats.mute"));
            binauralBeatsGenerator.start();

        } else {
            toggleBinauralBeatsButton.setText(translate("button.binaural.beats.unmute"));
            binauralBeatsGenerator.stop();
        }
    }

    public String translate(String key) {
        return bundle.getString(key);
    }


}
