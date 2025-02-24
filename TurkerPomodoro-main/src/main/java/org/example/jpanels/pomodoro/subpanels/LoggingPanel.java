/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package org.example.jpanels.pomodoro.subpanels;

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
