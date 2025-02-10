package org.example;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.jpanels.about.AboutPanel;
import org.example.jpanels.binaural.BinauralPanel;
import org.example.jpanels.calculator.CalculatorPanel;
import org.example.jpanels.datetime.DateTimePanel;
import org.example.jpanels.metronome.MetronomePanel;
import org.example.jpanels.mididevice.MidiInstrumentPanel;
import org.example.jpanels.mp3.Mp3PlayerFx;
import org.example.jpanels.noisegenerator.NoisePanel;
import org.example.jpanels.notes.NotesPanel;
import org.example.jpanels.paint.CanvasPanel;
import org.example.jpanels.piano.PianoPanel;
import org.example.jpanels.speakertest.AudioOutputPanel;
import org.example.jpanels.pomodoro.PomodoroMainPanel;
import org.example.jpanels.taptempo.TapTempoTool;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ApplicationFrame extends JFrame {

    private int frameWidth, frameHeight;


    JToggleButton toggleAlwaysOnTopButton, toggleHistoryLoggingButton;
    private boolean isAlwaysOnTop, isHistoryLoggingEnabled;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    public ApplicationFrame() {







        frameWidth = Integer.parseInt(props.getProperty("frame.width", "1024"));
        frameHeight = Integer.parseInt(props.getProperty("frame.height", "768"));



        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(frameWidth, frameHeight);
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
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.on"));
            setAlwaysOnTop(isAlwaysOnTop);
        } else {
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.off"));
        }


        JTabbedPane tabbedPanel = new JTabbedPane();

        PomodoroMainPanel pomodoroPanel = new PomodoroMainPanel();

        tabbedPanel.addTab("Pomodoro", pomodoroPanel);




        JTabbedPane jTabbedPaneForMp3 = new JTabbedPane();
        Mp3PlayerFx playerPanel = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player", playerPanel.getPlayerPanel());
        Mp3PlayerFx playerPanel2 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.2.file.location", "playlist2.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player2", playerPanel2.getPlayerPanel());
        Mp3PlayerFx playerPanel3 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.3.file.location", "playlist3.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        tabbedPanel.addTab("MP3 Players", jTabbedPaneForMp3);


        JTabbedPane jTabbedPaneForNoises = new JTabbedPane();

        NoisePanel noisePanel = new NoisePanel();
        jTabbedPaneForNoises.addTab("Noise Generator", noisePanel.getPlayerPanel());
        BinauralPanel binauralPanel = new BinauralPanel();
        jTabbedPaneForNoises.addTab(translate("tab.panel.binaural.beats.title"), binauralPanel);
        MetronomePanel metronomePanel = new MetronomePanel();
        jTabbedPaneForNoises.addTab("Metronome", metronomePanel.getPlayerPanel());


        tabbedPanel.addTab("Noise Generators", jTabbedPaneForNoises);




        JTabbedPane jTabbedPaneForOtherTools = new JTabbedPane();


        NotesPanel notesPanel = new NotesPanel();
        jTabbedPaneForOtherTools.addTab("Notes", notesPanel);
        CanvasPanel canvasPanel = new CanvasPanel();
        jTabbedPaneForOtherTools.addTab("Paint", canvasPanel);
        CalculatorPanel calculatorPanel = new CalculatorPanel();
        jTabbedPaneForOtherTools.addTab("Calculator", calculatorPanel);
        TapTempoTool tapTempoTool = new TapTempoTool();
        jTabbedPaneForOtherTools.addTab("Tap Tempo", tapTempoTool);
        PianoPanel pianoPanel = new PianoPanel();
        jTabbedPaneForOtherTools.addTab("Piano", pianoPanel);
        DateTimePanel dateTimePanel = new DateTimePanel();
        jTabbedPaneForOtherTools.addTab("Date Diff", dateTimePanel);

        tabbedPanel.addTab("Other Tools", jTabbedPaneForOtherTools);





        JTabbedPane jTabbedPaneForDeviceTesting = new JTabbedPane();
        MidiInstrumentPanel midiInstrumentPanel = new MidiInstrumentPanel();
        jTabbedPaneForDeviceTesting.add("MIDI test", midiInstrumentPanel);
        AudioOutputPanel audioOutputPanel = new AudioOutputPanel();
        jTabbedPaneForDeviceTesting.addTab("Speaker Test", audioOutputPanel);

        tabbedPanel.addTab("Device Tests", jTabbedPaneForDeviceTesting);






        JPanel applicationSettingsPanel = new JPanel();

        applicationSettingsPanel.add(toggleAlwaysOnTopButton);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        applicationSettingsPanel.add(toggleHistoryLoggingButton);

        tabbedPanel.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);

        AboutPanel aboutPanel = new AboutPanel();
        tabbedPanel.addTab("About", aboutPanel);






        add(tabbedPanel, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.

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
