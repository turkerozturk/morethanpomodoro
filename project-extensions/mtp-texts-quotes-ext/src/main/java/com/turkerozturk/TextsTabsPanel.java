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
package com.turkerozturk;

import javax.swing.*;

public class TextsTabsPanel extends JPanel implements PanelPlugin{


    public TextsTabsPanel() {

        JTabbedPane textPanels = new JTabbedPane();
        RandomTextDisplayPanel randomTextDisplayPanel1 = new RandomTextDisplayPanel(1);
        RandomTextDisplayPanel randomTextDisplayPanel2 = new RandomTextDisplayPanel(2);
        RandomTextDisplayPanel randomTextDisplayPanel3 = new RandomTextDisplayPanel(3);
        textPanels.addTab("Quotes", randomTextDisplayPanel1);
        textPanels.addTab("To Do", randomTextDisplayPanel2);
        textPanels.addTab("Memorize", randomTextDisplayPanel3);
        this.add(textPanels);
    }

    @Override
    public String getTabName() {
        return "plugin.texts.quotes.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
