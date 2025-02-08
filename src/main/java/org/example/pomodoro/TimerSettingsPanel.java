package org.example.pomodoro;

import org.example.PomodoroTimerState;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class TimerSettingsPanel extends JPanel {

    PomodoroTimerState pomodoroTimerState;
    private int remainingSeconds;


    JSpinner spinnerPomodoroWorkDuration, spinnerPomodoroShortBreak, spinnerPomodoroLongBreak;

    private int pomodoroWorkDuration, pomodoroShortBreak, pomodoroLongBreak;

    private Properties props = new Properties();

    private String language;
    private String country;

    private ResourceBundle bundle;


    public TimerSettingsPanel() {

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

        SpinnerModel spinnerModel3 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroWorkDuration = new JSpinner(spinnerModel3);
        spinnerPomodoroWorkDuration.setValue((int) pomodoroWorkDuration);
        spinnerPomodoroWorkDuration.addChangeListener(e -> changePomodoroWorkDuration());
        //spinnerPomodoroWorkDuration.setPreferredSize(new Dimension(100, 40));
        JPanel panel0 = new JPanel();
        panel0.add(new JLabel(bundle.getString("spinner.pomodoro.work.duration")));
        panel0.add(spinnerPomodoroWorkDuration);
        this.add(panel0);

        SpinnerModel spinnerModel4 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroShortBreak = new JSpinner(spinnerModel4);
        spinnerPomodoroShortBreak.setValue((int) pomodoroShortBreak);
        spinnerPomodoroShortBreak.addChangeListener(e -> changePomodoroShortBreak());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel1 = new JPanel();
        panel1.add(new JLabel(bundle.getString("spinner.pomodoro.short.break.duration")));
        panel1.add(spinnerPomodoroShortBreak);
        this.add(panel1);


        SpinnerModel spinnerModel5 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroLongBreak = new JSpinner(spinnerModel5);
        spinnerPomodoroLongBreak.setValue((int) pomodoroLongBreak);
        spinnerPomodoroLongBreak.addChangeListener(e -> changePomodoroLongBreak());
        //spinnerPomodoroLongBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel2 = new JPanel();
        panel2.add(new JLabel(bundle.getString("spinner.pomodoro.long.break.duration")));
        panel2.add(spinnerPomodoroLongBreak);
        this.add(panel2);

        this.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("tab.panel.timings.description")));

    }

    private void changePomodoroWorkDuration() {
        int oldPomodoroWorkDuration = pomodoroWorkDuration;
        pomodoroWorkDuration = (Integer) spinnerPomodoroWorkDuration.getValue();
        setRemainingSeconds(oldPomodoroWorkDuration, pomodoroWorkDuration, PomodoroTimerState.WORK_TIME);
    }

    private void changePomodoroShortBreak() {
        int oldPomodoroShortBreak = pomodoroShortBreak;
        pomodoroShortBreak = (Integer) spinnerPomodoroShortBreak.getValue();
        setRemainingSeconds(oldPomodoroShortBreak, pomodoroShortBreak, PomodoroTimerState.SHORT_BREAK);
    }

    private void changePomodoroLongBreak() {
        int oldPomodoroLongBreak = pomodoroLongBreak;
        pomodoroLongBreak = (Integer) spinnerPomodoroLongBreak.getValue();
        setRemainingSeconds(oldPomodoroLongBreak, pomodoroLongBreak, PomodoroTimerState.LONG_BREAK);
    }

    public void setRemainingSeconds(int oldDurationAsMinutes, int newDurationAsMinutes, PomodoroTimerState timerState) {
        int differenceAsSeconds = (oldDurationAsMinutes - newDurationAsMinutes) * 60;
        if (pomodoroTimerState == null) {
            pomodoroTimerState = timerState;

        }
        if (pomodoroTimerState.equals(timerState)) {
            if (remainingSeconds - differenceAsSeconds > 1) {
                remainingSeconds = remainingSeconds - differenceAsSeconds;
            }
        }
    }


}
