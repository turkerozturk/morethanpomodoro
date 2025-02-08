package org.example;

import org.example.jpanels.binaural.BinauralPanel;
import org.example.jpanels.metronome.MetronomePanel;
import org.example.jpanels.mididevice.MidiInstrumentPanel;
import org.example.jpanels.mp3.Mp3PlayerFx;
import org.example.jpanels.noisegenerator.NoisePanel;
import org.example.jpanels.speakertest.AudioOutputPanel;
import org.example.pomodoro.EndingSoundPanel;
import org.example.pomodoro.LoggingPanel;
import org.example.pomodoro.TickSoundPanel;
import org.example.pomodoro.TimerSettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class PomodoroFrame extends JFrame {


    private LoggingPanel loggingPanel;
    Properties props = new Properties();


    PomodoroTimerState pomodoroTimerState;


    private JLabel timeLabel, messageLabel;
    private JButton startButton, stopButton, resetButton, jumpToNextButton;
    JToggleButton toggleAlwaysOnTopButton, toggleAutoPlayButton, toggleHistoryLoggingButton;



    private Timer timer;

    private boolean isAutoPlay, isAlwaysOnTop, isHistoryLoggingEnabled;


    // dakika
    private int pomodoroWorkDuration, pomodoroShortBreak, pomodoroLongBreak;


    int wavSoundVolume, frequencySoundVolume;


    private String currentTimerLogMessage;

    private int remainingSeconds;
    private boolean toggleWorkSession = true;
    private int pomodoroCount = 0; // onemli. 0 olarak kalsin.


    private ResourceBundle bundle;
    private String language;
    private String country;
    private int endingSoundVolume;


    public PomodoroFrame() {

        loadConfig();  // config.properties oku

        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 300);
        setLocationRelativeTo(null);

        remainingSeconds = pomodoroWorkDuration * 60; // initial timer in minutes.


        JTabbedPane tabbedPane = new JTabbedPane();

        JTabbedPane jTabbedPaneForPomodoro = new JTabbedPane();


        JPanel pomodoroControlsPanel = new JPanel();


        pomodoroControlsPanel.add(startButton);
        pomodoroControlsPanel.add(stopButton);
        pomodoroControlsPanel.add(resetButton);
        pomodoroControlsPanel.add(jumpToNextButton);
        pomodoroControlsPanel.add(toggleAutoPlayButton);

        jTabbedPaneForPomodoro.addTab(translate("tab.panel.controls.title"), pomodoroControlsPanel);


        TimerSettingsPanel pomodoroTimingsPanel = new TimerSettingsPanel();

        jTabbedPaneForPomodoro.addTab(translate("tab.panel.timings.title"), pomodoroTimingsPanel);


        TickSoundPanel tickSoundPanel = new TickSoundPanel();


        jTabbedPaneForPomodoro.addTab(translate("tab.panel.tick.sound.title"), tickSoundPanel);


        EndingSoundPanel endingSoundPanel = new EndingSoundPanel();



        jTabbedPaneForPomodoro.addTab(translate("tab.panel.ending.sound.title"), endingSoundPanel);


        loggingPanel = new LoggingPanel();
        jTabbedPaneForPomodoro.addTab("Session Log", loggingPanel);


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


        // BURADAN asagisi frame in ust kismindaki sayac panalei. Sonra da JSplitepane ile ustteki ve alttaki ekleniyor.
        // TODO bu panel ve spliti al tek tabbedPane ye ekle.
        JPanel panelForTimer = new JPanel(new BorderLayout());

        JPanel timerSubPanel = new JPanel();
        timerSubPanel.setLayout(new BoxLayout(timerSubPanel, BoxLayout.Y_AXIS)); // Dikey hizalama

        timeLabel = new JLabel(formatTime(remainingSeconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // MERKEZE HİZALA

        messageLabel = new JLabel("Pomodoro", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // MERKEZE HİZALA

        timerSubPanel.add(timeLabel);
        timerSubPanel.add(messageLabel);

        panelForTimer.add(timerSubPanel, BorderLayout.CENTER);


        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelForTimer, jTabbedPaneForPomodoro);

        tabbedPane.addTab("Pomodoro", jSplitPane);


        add(tabbedPane, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.


        // Geri sayım
        timer = new Timer(1000, (ActionEvent e) -> {
            remainingSeconds--;
            timeLabel.setText(formatTime(remainingSeconds));

            // Her saniyede metronom kontrolü
            tickSoundPanel.tick();

            if (remainingSeconds <= 0) {
                stopTimer();
                endingSoundPanel.playFrequencyBeepIfSelected();
                cycleNext();
                if (isAutoPlay) {
                    appendMessageToHistory(getCurrentTimestamp() + "\tautoplay");
                    startTimer();
                } else {
                    // System.out.println("auto play disabled");
                }
            } else {
                // System.out.println(getCurrentTimerLogMessage()); // debug icin
            }
        });


        startButton.addActionListener(e -> startTimer());
        stopButton.addActionListener(e -> stopTimer());
        resetButton.addActionListener(e -> resetTimer());
        jumpToNextButton.addActionListener(e -> jumpToNextTimer());
        toggleAlwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop());
        toggleAutoPlayButton.addActionListener(e -> toggleAutoPlay());


    }


    private void toggleHistoryLogging() {
        isHistoryLoggingEnabled = !isHistoryLoggingEnabled;
        if (isHistoryLoggingEnabled) {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.on"));
        } else {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.off"));
        }
    }







    private void toggleAutoPlay() {

        if (toggleAutoPlayButton.isSelected()) {
            isAutoPlay = true;
            toggleAutoPlayButton.setText(translate("autoplay.on"));
        } else {
            isAutoPlay = false;
            toggleAutoPlayButton.setText(translate("autoplay.off"));
        }

    }

    private void toggleAlwaysOnTop() {
        boolean isSelected = toggleAlwaysOnTopButton.isSelected();
        setAlwaysOnTop(isSelected);
    }




    private void jumpToNextTimer() {
        stopTimer();

        cycleNext();


    }


    private void loadConfig() {


        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);

                language = props.getProperty("language.locale", "en");
                country = props.getProperty("language.country", "EN");

                Locale locale = new Locale(language, country);
                bundle = ResourceBundle.getBundle("messages", locale);

                startButton = new JButton(translate("timer.start"));
                stopButton = new JButton(translate("timer.stop"));
                resetButton = new JButton(translate("timer.reset"));
                jumpToNextButton = new JButton(translate("timer.next"));
                toggleAutoPlayButton = new JToggleButton(translate("autoplay.on"));
                toggleAlwaysOnTopButton = new JToggleButton(translate("frame.always.on.top"));
                toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

                int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
                isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
                toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);

                pomodoroWorkDuration = Integer.parseInt(props.getProperty("work.duration", "25"));
                pomodoroShortBreak = Integer.parseInt(props.getProperty("short.break", "5"));
                pomodoroLongBreak = Integer.parseInt(props.getProperty("long.break", "15"));


                wavSoundVolume = Integer.parseInt(props.getProperty("sound.wav.volume", "100")); // default volume level: 100

                frequencySoundVolume = Integer.parseInt(props.getProperty("sound.frequency.volume", "100"));

                endingSoundVolume = Integer.parseInt(props.getProperty("sound.ending.volume", "100"));


                int autoPlayAsInt = Integer.parseInt(props.getProperty("pomodoro.autoplay.toggle", "1"));
                isAutoPlay = (autoPlayAsInt == 1);
                toggleAutoPlayButton.setSelected(isAutoPlay);
                toggleAutoPlay();



                int alwaysOnTopAsInt = Integer.parseInt(props.getProperty("always.on.top.toggle", "1"));
                isAlwaysOnTop = (alwaysOnTopAsInt == 1);
                toggleAlwaysOnTopButton.setSelected(isAlwaysOnTop);
                if (toggleAlwaysOnTopButton.isSelected()) {
                    toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.on"));
                    setAlwaysOnTop(isAlwaysOnTop);
                } else {
                    toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.off"));
                }


            } else {
                // setDefaultValues();
            }
        } catch (IOException e) {
            //  setDefaultValues();
        }
    }

    /* config properties zaten jar icerisinde de olacagi icin buna gerek yok.
    private void setDefaultValues() {
        pomodoroWorkDuration = 25;
        pomodoroShortBreak = 5;
        pomodoroLongBreak = 15;
        metronomeInterval = 1;
        soundType = "WAV";
        soundFile = "beep.wav";
    }
    */

    private void startTimer() {
        if (!timer.isRunning()) {
            // currentTimerLogMessage = getCurrentTimestamp() + "\t" + (pomodoroCount +1) + "\t" +
            //         pomodoroWorkDuration + "\t" + toggleWorkSession;

            if (isHistoryLoggingEnabled) {
                appendMessageToHistory(getCurrentTimerLogMessage());
            }

            messageLabel.setText(getCurrentTimerScreenMessage());
            loggingPanel.appendLog(getCurrentTimerScreenMessage());

            timer.start();


            startButton.setText(translate("timer.start"));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            resetButton.setEnabled(false);
        }


    }

    private void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
            //pomodoroTimerState = PomodoroTimerState.STOPPED;
            startButton.setText(translate("timer.start.continue"));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            resetButton.setEnabled(true);

        }
    }

    private void resetTimer() {
        stopTimer();
        pomodoroTimerState = PomodoroTimerState.STOPPED;
        remainingSeconds = pomodoroWorkDuration * 60; // Süreyi başa al
        timeLabel.setText(formatTime(remainingSeconds));
    }

    private void cycleNext() {
        // Pomodoro turu bittiğinde mola veya yeni çalışma süresi ayarla
        if (toggleWorkSession) {
            pomodoroCount++;
            if (pomodoroCount % 4 == 0) {
                pomodoroTimerState = PomodoroTimerState.LONG_BREAK;
                remainingSeconds = pomodoroLongBreak * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroLongBreak + ", " + inWorkSession;


            } else {
                pomodoroTimerState = PomodoroTimerState.SHORT_BREAK;
                remainingSeconds = pomodoroShortBreak * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroShortBreak + ", " + inWorkSession;
                appendMessageToHistory(getCurrentTimerLogMessage());

            }
        } else {
            pomodoroTimerState = PomodoroTimerState.WORK_TIME;
            remainingSeconds = pomodoroWorkDuration * 60;
            //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroWorkDuration + ", " + inWorkSession;
            appendMessageToHistory(getCurrentTimerLogMessage());

        }
        toggleWorkSession = !toggleWorkSession;
        timeLabel.setText(formatTime(remainingSeconds));

        if (isHistoryLoggingEnabled) {
            appendMessageToHistory(getCurrentTimerLogMessage());
        }


    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getCurrentTimerLogMessage() {
        currentTimerLogMessage = String.format("%s\t%d\t%d\t%s\t%b", getCurrentTimestamp(), pomodoroCount
                , remainingSeconds, pomodoroTimerState, toggleWorkSession);

        return currentTimerLogMessage;
    }

    public String getCurrentTimerScreenMessage() {
        currentTimerLogMessage = String.format("Pomodoro %d of 4 %s", pomodoroCount
                , pomodoroTimerState);

        return currentTimerLogMessage;
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
