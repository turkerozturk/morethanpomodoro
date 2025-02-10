package org.example.jpanels.pomodoro;

import javax.swing.*;

public class LoggingPanel extends JPanel {


    private final JTextArea jTextAreaForPomodoroSessionLog;

    public LoggingPanel() {

        jTextAreaForPomodoroSessionLog = new JTextArea();
        //jTextAreaForPomodoroSessionLog.setMinimumSize(new Dimension(600, 200));
        //jTextAreaForPomodoroSessionLog.setMaximumSize(new Dimension(600, 200));

        jTextAreaForPomodoroSessionLog.setRows(7);
        jTextAreaForPomodoroSessionLog.setColumns(40);
        jTextAreaForPomodoroSessionLog.setLineWrap(false);

        this.add(jTextAreaForPomodoroSessionLog);



    }

    public void appendLog(String message) {

        jTextAreaForPomodoroSessionLog.append(message + "\n");

    }

}
