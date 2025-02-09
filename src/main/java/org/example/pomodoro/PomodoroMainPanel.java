package org.example.pomodoro;

import org.example.FileUtil;
import org.example.PomodoroTimerState;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class PomodoroMainPanel extends JPanel  implements TimerSettingsListener{

    private LoggingPanel loggingPanel;

    private int pomodoroLimit; // TODO make adjustable by config file.


    PomodoroTimerState pomodoroTimerState;
    TimerSettingsPanel pomodoroTimingsPanel;

    JToggleButton toggleAutoPlayButton;

    private JButton startButton, stopButton, resetButton, jumpToNextButton;

    private Timer timer;

    private boolean isAutoPlay, isAlwaysOnTop, isHistoryLoggingEnabled;

    int wavSoundVolume, frequencySoundVolume;

    private String currentTimerLogMessage;

    private int remainingSeconds;
    private boolean toggleWorkSession = true;
    private int pomodoroCurrentNumber = 0; // onemli. 0 olarak kalsin.

    TimerPanel timerPanel;

    LanguageManager langManager = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    private String currentTimerScreenMessage;

    public PomodoroMainPanel() {


        pomodoroLimit = Integer.parseInt(ConfigManager.getInstance().getProperty("default.pomodoro.cycle.count","4"));
        pomodoroTimerState = PomodoroTimerState.WORK_TIME;



       // Locale locale = new Locale(language, country);
       // bundle = ResourceBundle.getBundle("messages", locale);

        startButton = new JButton(translate("timer.start"));
        stopButton = new JButton(translate("timer.stop"));
        resetButton = new JButton(translate("timer.reset"));
        jumpToNextButton = new JButton(translate("timer.next"));
        toggleAutoPlayButton = new JToggleButton(translate("autoplay.on"));

        wavSoundVolume = Integer.parseInt(props.getProperty("sound.wav.volume", "100")); // default volume level: 100

        frequencySoundVolume = Integer.parseInt(props.getProperty("sound.frequency.volume", "100"));

        //endingSoundVolume = Integer.parseInt(props.getProperty("sound.ending.volume", "100"));


        int autoPlayAsInt = Integer.parseInt(props.getProperty("pomodoro.autoplay.toggle", "1"));
        isAutoPlay = (autoPlayAsInt == 1);
        toggleAutoPlayButton.setSelected(isAutoPlay);
        toggleAutoPlay();



        JTabbedPane jTabbedPaneForPomodoro = new JTabbedPane();

        timerPanel = new TimerPanel();
        pomodoroTimingsPanel = new TimerSettingsPanel(timerPanel);
        remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration() * 60; // initial timer in minutes.
        timerPanel.setRemainingSeconds(remainingSeconds);

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

        setScreenMessageForWorkTime(); // todo
        System.out.println(timerPanel.getMessageLabel().getText());
        timerPanel.getMessageLabel().setText("dssdsdf");

        // Geri sayım
        timer = new Timer(1000, (ActionEvent e) -> {
            remainingSeconds--;
            timerPanel.getTimeLabel().setText(formatTime(remainingSeconds));


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






       // JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel jSplitPane = new JPanel();
        jSplitPane.setLayout(new BoxLayout(jSplitPane, BoxLayout.Y_AXIS));
        jSplitPane.add(timerPanel);
        jSplitPane.add(pomodoroControlsPanel);
        jSplitPane.add(jTabbedPaneForPomodoro);

        this.add(jSplitPane);

        // TimerSettingsPanel'e kendi listener'ımızı setliyoruz
        pomodoroTimingsPanel.setTimerSettingsListener(this);


        setScreenMessageForWorkTime();

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

            //timerPanel.getMessageLabel().setText(getCurrentTimerScreenMessage());
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
        switch(pomodoroTimerState) {
            case STOPPED:
                break;
            case PAUSED:
                break;
            case WORK_TIME:
                remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration()  * 60; // Süreyi başa al
                break;
            case SHORT_BREAK:
                remainingSeconds = pomodoroTimingsPanel.getPomodoroShortBreak()  * 60; // Süreyi başa al
                break;
            case LONG_BREAK:
                remainingSeconds = pomodoroTimingsPanel.getPomodoroLongBreak()  * 60; // Süreyi başa al
                break;
        }

        pomodoroTimerState = PomodoroTimerState.STOPPED;
        timerPanel.getTimeLabel().setText(formatTime(remainingSeconds));
    }

    private void cycleNext() {
        // Pomodoro turu bittiğinde mola veya yeni çalışma süresi ayarla
        if (toggleWorkSession) {
            pomodoroCurrentNumber++;
            if (pomodoroCurrentNumber % pomodoroLimit == 0) {
                pomodoroCurrentNumber = 0; // birkac pomodoroluk bir dongu bitince sayac bastan baslar cunku.
                pomodoroTimerState = PomodoroTimerState.LONG_BREAK;
                remainingSeconds = pomodoroTimingsPanel.getPomodoroLongBreak() * 60;
                setCurrentTimerScreenMessage(String.format("%s", langManager.getString("LONG_BREAK")));
                appendMessageToHistory(getCurrentTimerLogMessage());


            } else {
                pomodoroTimerState = PomodoroTimerState.SHORT_BREAK;
                remainingSeconds = pomodoroTimingsPanel.getPomodoroShortBreak() * 60;
                setCurrentTimerScreenMessage(String.format("%s", langManager.getString("SHORT_BREAK")));
                appendMessageToHistory(getCurrentTimerLogMessage());

            }
        } else {
            pomodoroTimerState = PomodoroTimerState.WORK_TIME;
            remainingSeconds = pomodoroTimingsPanel.getPomodoroWorkDuration() * 60;
            setScreenMessageForWorkTime();


            //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroWorkDuration + ", " + inWorkSession;
            appendMessageToHistory(getCurrentTimerLogMessage());

        }
        toggleWorkSession = !toggleWorkSession;
        timerPanel.getTimeLabel().setText(formatTime(remainingSeconds));

        if (isHistoryLoggingEnabled) {
            appendMessageToHistory(getCurrentTimerLogMessage());
        }


    }

    private void setScreenMessageForWorkTime() {
        String message = String.format(langManager.getString("timer.screen.message.format")
                , langManager.getString("WORK_TIME"), pomodoroCurrentNumber + 1, pomodoroLimit);
        setCurrentTimerScreenMessage(message);
        System.out.println(message);
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
        currentTimerLogMessage = String.format("%s\t%d\t%d\t%s\t%b", getCurrentTimestamp(), pomodoroCurrentNumber
                , remainingSeconds, pomodoroTimerState, toggleWorkSession);

        return currentTimerLogMessage;
    }

    public String getCurrentTimerScreenMessage() {


        return currentTimerScreenMessage;
    }



    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
    }


    private void jumpToNextTimer() {
        stopTimer();

        cycleNext();
        timerPanel.getMessageLabel().setText(getCurrentTimerScreenMessage());


    }

    public String translate(String key) {
       // return bundle.getString(key);
        return langManager.getString(key);
    }

    // TimerSettingsListener arayüzünde tanımladığımız metodu implemente ediyoruz
    @Override
    public void pomodoroWorkDurationChanged(int newValue) {
        // Spinner değerinin değiştiğini burada yakalıyoruz
        System.out.println("Yeni pomodoro süresi: " + newValue);

        // TODO bunu uygun mesajla degistir
        //  timerPanel.getMessageLabel().setText(String.valueOf(newValue));
        remainingSeconds = timerPanel.getRemainingSeconds();
        System.out.println(remainingSeconds);

        // Burada üst panelde istediğiniz işlemleri yapabilirsiniz
    }


    public void setCurrentTimerScreenMessage(String currentTimerScreenMessage) {
        this.currentTimerScreenMessage = currentTimerScreenMessage;
        timerPanel.getMessageLabel().setText(currentTimerScreenMessage);
    }
}
