package org.example.jpanels.metronome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MetronomePanel extends JPanel {
    private JSpinner bpmSpinner;
    private JToggleButton startStopButton;
    private JSlider volumeSlider;
    private Metronome metronome;

    public JPanel getPlayerPanel() {
        return this;
    }

    public MetronomePanel() {
        setLayout(new BorderLayout());

        bpmSpinner = new JSpinner(new SpinnerNumberModel(120, 30, 300, 1));
        startStopButton = new JToggleButton("Start");
        volumeSlider = new JSlider(0, 100, 50);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("BPM:"));
        controlPanel.add(bpmSpinner);
        controlPanel.add(startStopButton);
        controlPanel.add(new JLabel("Volume:"));
        controlPanel.add(volumeSlider);

        add(controlPanel, BorderLayout.NORTH);

        metronome = new Metronome();

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startStopButton.isSelected()) {
                    startMetronome();
                } else {
                    stopMetronome();
                }
            }
        });

        bpmSpinner.addChangeListener(e -> metronome.setBPM((Integer) bpmSpinner.getValue()));
        volumeSlider.addChangeListener(e -> metronome.setVolume(volumeSlider.getValue() / 100.0f));
    }

    private void startMetronome() {
        metronome.start((Integer) bpmSpinner.getValue());
        startStopButton.setText("Stop");
    }

    private void stopMetronome() {
        metronome.stop();
        startStopButton.setText("Start");
    }
}
