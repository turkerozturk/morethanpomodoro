package org.example.jpanels.equaliser;

import javafx.application.Platform;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EqualiserPanel extends JPanel {
    private List<JSlider> sliders = new ArrayList<>();
    private static final int[] FREQUENCIES = {31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000}; // Hz
    private MediaPlayer mediaPlayer;

    public EqualiserPanel(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        setLayout(new GridLayout(1, FREQUENCIES.length, 5, 5));

        for (int i = 0; i < FREQUENCIES.length; i++) {
            int frequency = FREQUENCIES[i];
            JSlider slider = createSlider(frequency);
            sliders.add(slider);
            add(slider);
        }

        applyEqualizerSettings();
    }

    private JSlider createSlider(int frequency) {
        JSlider slider = new JSlider(JSlider.VERTICAL, -12, 12, 0); // -12dB ile +12dB arası
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(6);
        slider.setMinorTickSpacing(3);
        slider.setToolTipText(frequency + " Hz");

        slider.addChangeListener(e -> applyEqualizerSettings());

        return slider;
    }

    private void applyEqualizerSettings() {

        if (mediaPlayer == null) return;

        Platform.runLater(() -> {
            AudioEqualizer equalizer = mediaPlayer.getAudioEqualizer();

            for (int i = 0; i < sliders.size(); i++) {
                double gain = sliders.get(i).getValue(); // Slider değeri dB olarak
                equalizer.getBands().get(i).setGain(gain);

            }
        });
    }
}

