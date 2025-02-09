package org.example;

import org.example.jpanels.binaural.BinauralPanel;
import org.example.jpanels.metronome.MetronomePanel;
import org.example.jpanels.mididevice.MidiInstrumentPanel;
import org.example.jpanels.mp3.Mp3PlayerFx;
import org.example.jpanels.noisegenerator.NoisePanel;
import org.example.jpanels.speakertest.AudioOutputPanel;
import org.example.pomodoro.LoggingPanel;
import org.example.pomodoro.PomodoroPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ApplicationFrame extends JFrame {

    JToggleButton toggleAlwaysOnTopButton, toggleHistoryLoggingButton;
    private boolean isAlwaysOnTop, isHistoryLoggingEnabled;
    Properties props = new Properties();
    private ResourceBundle bundle;
    private String language;
    private String country;

    public ApplicationFrame() {






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

        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 400);
        setLocationRelativeTo(null);


        toggleAlwaysOnTopButton = new JToggleButton(translate("frame.always.on.top"));
        toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

        int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
        isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
        toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);


        int alwaysOnTopAsInt = Integer.parseInt(props.getProperty("always.on.top.toggle", "1"));
        isAlwaysOnTop = (alwaysOnTopAsInt == 1);
        toggleAlwaysOnTopButton.setSelected(isAlwaysOnTop);
        if (toggleAlwaysOnTopButton.isSelected()) {
            toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.on"));
            setAlwaysOnTop(isAlwaysOnTop);
        } else {
            toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.off"));
        }


        JTabbedPane tabbedPane = new JTabbedPane();


        JPanel applicationSettingsPanel = new JPanel();

        applicationSettingsPanel.add(toggleAlwaysOnTopButton);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        applicationSettingsPanel.add(toggleHistoryLoggingButton);

        tabbedPane.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);

        JTabbedPane jTabbedPaneForMp3 = new JTabbedPane();
        Mp3PlayerFx playerPanel = new Mp3PlayerFx("playlist1.txt");
        jTabbedPaneForMp3.addTab("MP3 Player", playerPanel.getPlayerPanel());
        Mp3PlayerFx playerPanel2 = new Mp3PlayerFx("playlist2.txt");
        jTabbedPaneForMp3.addTab("MP3 Player2", playerPanel2.getPlayerPanel());
        Mp3PlayerFx playerPanel3 = new Mp3PlayerFx("playlist3.txt");
        jTabbedPaneForMp3.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        tabbedPane.addTab("MP3 Player", jTabbedPaneForMp3);


        JTabbedPane jTabbedPaneForNoises = new JTabbedPane();

        NoisePanel noisePanel = new NoisePanel();
        jTabbedPaneForNoises.addTab("Noise Generator", noisePanel.getPlayerPanel());
        MetronomePanel metronomePanel = new MetronomePanel();
        jTabbedPaneForNoises.add("Metronome", metronomePanel.getPlayerPanel());


        BinauralPanel binauralPanel = new BinauralPanel();
        jTabbedPaneForNoises.addTab(translate("tab.panel.binaural.beats.title"), binauralPanel);


        tabbedPane.addTab("Noise Generators", jTabbedPaneForNoises);


        JTabbedPane jTabbedPaneForDeviceTesting = new JTabbedPane();
        MidiInstrumentPanel midiInstrumentPanel = new MidiInstrumentPanel();
        jTabbedPaneForDeviceTesting.add("MIDI test", midiInstrumentPanel);
        AudioOutputPanel audioOutputPanel = new AudioOutputPanel();
        jTabbedPaneForDeviceTesting.addTab("Speaker Test", audioOutputPanel);

        tabbedPane.addTab("Device Tests", jTabbedPaneForDeviceTesting);


        PomodoroPanel pomodoroPanel = new PomodoroPanel();

        tabbedPane.addTab("Pomodoro", pomodoroPanel);


        add(tabbedPane, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.

        toggleAlwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop());


    }


    private void toggleHistoryLogging() {
        isHistoryLoggingEnabled = !isHistoryLoggingEnabled;
        if (isHistoryLoggingEnabled) {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.on"));
        } else {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.off"));
        }
    }


    private void toggleAlwaysOnTop() {
        boolean isSelected = toggleAlwaysOnTopButton.isSelected();
        setAlwaysOnTop(isSelected);
    }






    public String translate(String key) {
        return bundle.getString(key);
    }

    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
    }

}
