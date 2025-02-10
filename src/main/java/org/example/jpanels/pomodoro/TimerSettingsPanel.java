package org.example.jpanels.pomodoro;

import org.example.PomodoroTimerState;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;

public class TimerSettingsPanel extends JPanel {

    private TimerSettingsListener listener;

    // Üst panelin timer settings eventlerini dinlemesi için
    public void setTimerSettingsListener(TimerSettingsListener listener) {
        this.listener = listener;
    }

    PomodoroTimerState pomodoroTimerState;
    private int remainingSeconds;


    JSpinner spinnerPomodoroWorkDuration, spinnerPomodoroShortBreak, spinnerPomodoroLongBreak;

    private int pomodoroWorkDuration, pomodoroShortBreak, pomodoroLongBreak;




    TimerPanel timerPanel;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    public TimerSettingsPanel(TimerPanel timerPanel) {

        this.timerPanel = timerPanel;





        pomodoroWorkDuration = Integer.parseInt(props.getProperty("work.duration", "25"));
        pomodoroShortBreak = Integer.parseInt(props.getProperty("short.break", "5"));
        pomodoroLongBreak = Integer.parseInt(props.getProperty("long.break", "15"));



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
      //  System.out.printf("%d %d %s%n", oldDurationAsMinutes, newDurationAsMinutes, timerState);

        int differenceAsSeconds = (oldDurationAsMinutes - newDurationAsMinutes) * 60;
        if (pomodoroTimerState == null) {
            pomodoroTimerState = timerState;
           // System.out.println("pomodoroTimerState == null, " + timerState);
        }
        if (pomodoroTimerState.equals(timerState)) {
           // System.out.println("pomodoroTimerState.equals(timerState), " + timerState);
            //System.out.println(this.timerPanel.getRemainingSeconds());
           // this.timerPanel.setRemainingSeconds(1000);

            if (remainingSeconds - differenceAsSeconds > 1) {
                remainingSeconds = remainingSeconds - differenceAsSeconds;
                System.out.println(remainingSeconds);
               // System.out.println(remainingSeconds);
                this.timerPanel.setRemainingSeconds(remainingSeconds);
            }
            /*
            if (listener != null) {
                int newValue = (int) spinnerPomodoroWorkDuration.getValue();
                listener.pomodoroWorkDurationChanged(newValue);
                System.out.println(newValue);
            }
*/



        }
    }

    public int getPomodoroWorkDuration() {
        return pomodoroWorkDuration;
    }

    public void setPomodoroWorkDuration(int pomodoroWorkDuration) {
        this.pomodoroWorkDuration = pomodoroWorkDuration;
    }

    public int getPomodoroShortBreak() {
        return pomodoroShortBreak;
    }

    public void setPomodoroShortBreak(int pomodoroShortBreak) {
        this.pomodoroShortBreak = pomodoroShortBreak;
    }

    public int getPomodoroLongBreak() {
        return pomodoroLongBreak;
    }

    public void setPomodoroLongBreak(int pomodoroLongBreak) {
        this.pomodoroLongBreak = pomodoroLongBreak;
    }
}
