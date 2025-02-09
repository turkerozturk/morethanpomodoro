package org.example.pomodoro;

import org.example.FileUtil;
import org.example.PomodoroTimerState;

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



public class PomodoroPanel extends JPanel {

    private LoggingPanel loggingPanel;


    PomodoroTimerState pomodoroTimerState;
    TimerSettingsPanel pomodoroTimingsPanel;

    JToggleButton toggleAutoPlayButton;

    private JLabel timeLabel, messageLabel;
    private JButton startButton, stopButton, resetButton, jumpToNextButton;



    private Timer timer;

    private boolean isAutoPlay, isAlwaysOnTop, isHistoryLoggingEnabled;



    private Properties props = new Properties();

    private String language;
    private String country;

    private ResourceBundle bundle;

    int wavSoundVolume, frequencySoundVolume;


    private String currentTimerLogMessage;

    private int remainingSeconds;
    private boolean toggleWorkSession = true;
    private int pomodoroCount = 0; // onemli. 0 olarak kalsin.

    private int endingSoundVolume;


    public PomodoroPanel() {

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

        startButton = new JButton(translate("timer.start"));
        stopButton = new JButton(translate("timer.stop"));
        resetButton = new JButton(translate("timer.reset"));
        jumpToNextButton = new JButton(translate("timer.next"));
        toggleAutoPlayButton = new JToggleButton(translate("autoplay.on"));

        wavSoundVolume = Integer.parseInt(props.getProperty("sound.wav.volume", "100")); // default volume level: 100

        frequencySoundVolume = Integer.parseInt(props.getProperty("sound.frequency.volume", "100"));

        endingSoundVolume = Integer.parseInt(props.getProperty("sound.ending.volume", "100"));


        int autoPlayAsInt = Integer.parseInt(props.getProperty("pomodoro.autoplay.toggle", "1"));
        isAutoPlay = (autoPlayAsInt == 1);
        toggleAutoPlayButton.setSelected(isAutoPlay);
        toggleAutoPlay();



        JTabbedPane jTabbedPaneForPomodoro = new JTabbedPane();


        pomodoroTimingsPanel = new TimerSettingsPanel();

        remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration() * 60; // initial timer in minutes.

        jTabbedPaneForPomodoro.addTab(translate("tab.panel.timings.title"), pomodoroTimingsPanel);



        JPanel pomodoroControlsPanel = new JPanel();


        pomodoroControlsPanel.add(startButton);
        pomodoroControlsPanel.add(stopButton);
        pomodoroControlsPanel.add(resetButton);
        pomodoroControlsPanel.add(jumpToNextButton);
        pomodoroControlsPanel.add(toggleAutoPlayButton);

        //jTabbedPaneForPomodoro.addTab(translate("tab.panel.controls.title"), pomodoroControlsPanel);





        TickSoundPanel tickSoundPanel = new TickSoundPanel();


        jTabbedPaneForPomodoro.addTab(translate("tab.panel.tick.sound.title"), tickSoundPanel);


        EndingSoundPanel endingSoundPanel = new EndingSoundPanel();



        jTabbedPaneForPomodoro.addTab(translate("tab.panel.ending.sound.title"), endingSoundPanel);


        loggingPanel = new LoggingPanel();
        jTabbedPaneForPomodoro.addTab("Session Log", loggingPanel);



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
        toggleAutoPlayButton.addActionListener(e -> toggleAutoPlay());


        // BURADAN asagisi frame in ust kismindaki sayac panalei. Sonra da JSplitepane ile ustteki ve alttaki ekleniyor.
        // TODO bu panel ve spliti al tek tabbedPane ye ekle.
        JPanel panelForTimer = new JPanel();

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


       // JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel jSplitPane = new JPanel();
        jSplitPane.setLayout(new BoxLayout(jSplitPane, BoxLayout.Y_AXIS));
        jSplitPane.add(panelForTimer);
        jSplitPane.add(pomodoroControlsPanel);
        jSplitPane.add(jTabbedPaneForPomodoro);

        this.add(jSplitPane);


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
        remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration()  * 60; // Süreyi başa al
        timeLabel.setText(formatTime(remainingSeconds));
    }

    private void cycleNext() {
        // Pomodoro turu bittiğinde mola veya yeni çalışma süresi ayarla
        if (toggleWorkSession) {
            pomodoroCount++;
            if (pomodoroCount % 4 == 0) {
                pomodoroTimerState = PomodoroTimerState.LONG_BREAK;
                remainingSeconds = pomodoroTimingsPanel.getPomodoroLongBreak() * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroLongBreak + ", " + inWorkSession;


            } else {
                pomodoroTimerState = PomodoroTimerState.SHORT_BREAK;
                remainingSeconds = pomodoroTimingsPanel.getPomodoroShortBreak() * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroShortBreak + ", " + inWorkSession;
                appendMessageToHistory(getCurrentTimerLogMessage());

            }
        } else {
            pomodoroTimerState = PomodoroTimerState.WORK_TIME;
            remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration() * 60;
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



    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
    }


    private void jumpToNextTimer() {
        stopTimer();

        cycleNext();


    }

    public String translate(String key) {
        return bundle.getString(key);
    }


}
