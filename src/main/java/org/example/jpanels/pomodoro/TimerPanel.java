package org.example.jpanels.pomodoro;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {

    private int remainingSeconds;
    private JLabel timeLabel, messageLabel;


    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    public TimerPanel() {

       // JPanel panelForTimer = new JPanel();

        JPanel timerSubPanel = new JPanel();
        timerSubPanel.setLayout(new BoxLayout(timerSubPanel, BoxLayout.Y_AXIS));

        remainingSeconds = Integer.parseInt(props.getProperty("work.duration", "25")) * 60;
        //timeLabel = new JLabel(formatTime(remainingSeconds), SwingConstants.CENTER);
        timeLabel = new JLabel(formatTime(remainingSeconds), SwingConstants.CENTER);

        timeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("Pomodoro", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerSubPanel.add(timeLabel);
        timerSubPanel.add(messageLabel);

        add(timerSubPanel, BorderLayout.CENTER);

    }


    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
        timeLabel.setText(formatTime(remainingSeconds));
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public void setMessageLabel(JLabel messageLabel) {
        this.messageLabel = messageLabel;
    }









}
