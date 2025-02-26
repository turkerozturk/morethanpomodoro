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
package org.example.thirdparty.utils;
/*
All of the following code is taken from the software source code at
https://github.com/Velliz/Musicplayer, specifically from the commit dated
May 24, 2017 (https://github.com/Velliz/Musicplayer/commit/41c4c5ee21a21a845865c34c2b847c75d3349604),
and according to the same source, it is licensed under the
Apache License Version 2.0, January 2004. http://www.apache.org/licenses/
*/

import javax.swing.*;
import java.awt.*;

public class StatusFrame extends JFrame {

    private JTextArea txtArea = new JTextArea();
    int WIDTH = 500;
    int HEIGHT = 400;

    public StatusFrame() {
        super();
        setTitle("Status Frame");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        txtArea.setBounds(0,0,WIDTH,HEIGHT);
        JScrollPane sp = new JScrollPane(txtArea);
        sp.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(sp);
        
        setLocationRelativeTo(null);
    }

    public void addText(String text) {
        String app = txtArea.getText() + "\n";
        txtArea.setText(app + text);
    }

}
