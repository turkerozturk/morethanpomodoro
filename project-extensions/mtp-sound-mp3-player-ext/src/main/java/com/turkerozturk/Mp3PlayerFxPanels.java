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

import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;

public class Mp3PlayerFxPanels extends JPanel implements PanelPlugin {

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    private Mp3PlayerFx playerPanel;
    private Mp3PlayerFx playerPanel2;
    private Mp3PlayerFx playerPanel3;
    public Mp3PlayerFxPanels() {


        JTabbedPane jTabbedPaneForMp3 = new JTabbedPane();
        playerPanel = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player", playerPanel.getPlayerPanel());
        playerPanel2 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.2.file.location", "playlist2.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player2", playerPanel2.getPlayerPanel());
        playerPanel3 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.3.file.location", "playlist3.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        this.add(jTabbedPaneForMp3);

       // Mp3PlayerPanel mp3PlayerPanel = new Mp3PlayerPanel(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));

       // tabbedPanel.addTab("New MP3", mp3PlayerPanel);



    }

    @Override
    public String getTabName() {
        return "plugin.sound.mp3.player.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
}
