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
package org.example.equaliser;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class EqualizerApp extends JFrame {
    private MediaPlayer mediaPlayer;

    public EqualizerApp() {
        setTitle("MP3 Equalizer Player");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JFXPanel jfxPanel = new JFXPanel(); // JavaFX ile entegrasyon
        add(jfxPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");

        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        // JavaFX MediaPlayer başlat
        Platform.runLater(() -> {
            File file = new File("C:\\Users\\u\\Downloads\\kisisel\\Jim Power Stage 1 Metal Remix [vvTPR2B6dmo].m4a"); // MP3 dosya yolu
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(false);

            // JavaFX MediaView ekle
            MediaView mediaView = new MediaView(mediaPlayer);
            jfxPanel.setScene(new javafx.scene.Scene(new javafx.scene.layout.StackPane(mediaView)));

            // EqualizerPanel ekle
            EqualiserPanel equalizerPanel = new EqualiserPanel(mediaPlayer);
            add(equalizerPanel, BorderLayout.EAST);
            revalidate();
            repaint();
        });

        // Butonlar için event listener'lar
        playButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.play()));
        stopButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.stop()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EqualizerApp().setVisible(true));
    }
}

