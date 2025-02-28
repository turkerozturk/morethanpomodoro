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

import javafx.embed.swing.JFXPanel;
import com.turkerozturk.equaliser.EqualiserPanel;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;
import com.turkerozturk.playlist.PlayListManagerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Mp3PlayerPanel extends JPanel implements SoundController {

    private final Mp3PlayerFxService playerService;
    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();

    private final JFXPanel jfxPanel; // For JavaFX initialization
    private JButton playPauseButton, stopButton, nextButton, prevButton;
    private JButton fastButton, slowButton, playlistButton;
    private JLabel timeLabel;
    private JSlider seekSlider;
    private JSlider volumeSlider;
    private JLabel songLabel;
    private Timer timer;

    // Optional: for an equalizer
    private JFrame equalizerFrame;
    private EqualiserPanel equalizerPanel;
    private boolean isEqualizerEnabled = true;

    public Mp3PlayerPanel(String playlistFileLocation) {
        this.playerService = new Mp3PlayerFxService(playlistFileLocation);
        // Initialize JavaFX
        jfxPanel = new JFXPanel();
        initUI();
        initTimer();
        // Start listening for JavaFX changes if needed
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top-level panel to arrange components
        JPanel verticalPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        verticalPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        // Playlist button
        playlistButton = new JButton("Manage Playlist");
        playlistButton.addActionListener(e -> showPlayListPanel());
        verticalPanel.add(playlistButton);

        // Time area
        timeLabel = new JLabel("00:00:00 / 00:00:00", SwingConstants.CENTER);
        seekSlider = new JSlider(0, 100, 0);
        seekSlider.setPreferredSize(new Dimension(300, 20));
        seekSlider.addChangeListener(e -> {
            if (!seekSlider.getValueIsAdjusting()) {
                // Only seek when user releases the slider
                double percent = seekSlider.getValue() / 100.0;
                playerService.seekToPosition(percent);
                updateTimeLabel();
            }
        });

        JPanel timePanel = new JPanel();
        timePanel.add(seekSlider);
        timePanel.add(timeLabel);
        verticalPanel.add(timePanel);

        // Song label
        songLabel = new JLabel(bundle.getString("mp3.not.loaded"));
        String firstSong = playerService.getCurrentSongPath();
        if (firstSong != null) {
            songLabel.setText("Now playing: " + firstSong);
        }
        verticalPanel.add(songLabel);

        // Playback controls
        JPanel controlsPanel = new JPanel();
        playPauseButton = new JButton("▶ Play");
        stopButton = new JButton("■ Stop");
        nextButton = new JButton("⏭ Next");
        prevButton = new JButton("⏮ Previous");

        playPauseButton.addActionListener(e -> onPlayPause());
        stopButton.addActionListener(e -> onStop());
        nextButton.addActionListener(e -> onNext());
        prevButton.addActionListener(e -> onPrev());

        controlsPanel.add(prevButton);
        controlsPanel.add(playPauseButton);
        controlsPanel.add(stopButton);
        controlsPanel.add(nextButton);

        verticalPanel.add(controlsPanel);

        // Volume + speed
        JPanel volumePanel = new JPanel();
        volumePanel.add(new JLabel(bundle.getString("mp3.sound.volume")));

        volumeSlider = new JSlider(0, 100, 100); // default 100 = 1.0f
        volumeSlider.addChangeListener(e -> playerService.setVolume(volumeSlider.getValue()));
        volumePanel.add(volumeSlider);

        fastButton = new JButton("1.5x");
        slowButton = new JButton("0.5x");
        fastButton.addActionListener(e -> playerService.setPlaybackRate(1.5));
        slowButton.addActionListener(e -> playerService.setPlaybackRate(0.5));

        volumePanel.add(fastButton);
        volumePanel.add(slowButton);

        verticalPanel.add(volumePanel);

        // Wrap everything
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(verticalPanel);

        add(wrapperPanel, BorderLayout.CENTER);

        // Add the JavaFX panel so it initializes the toolkit (if needed)
        add(jfxPanel, BorderLayout.PAGE_START);
    }

    private void initTimer() {
        timer = new Timer(1000, (ActionEvent e) -> {
            // Update time label once per second
            updateTimeLabel();
            updateSeekSlider();
        });
    }

    private void onPlayPause() {
        // Start or toggle playback
        playerService.startPlayback(this::refreshUI);
        // Start the timer if playing, stop it if not
        if (playerService.isPlaying()) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    private void onStop() {
        playerService.stopPlayback(this::refreshUI);
        timer.stop();
    }

    private void onNext() {
        playerService.nextSong(this::refreshUI);
    }

    private void onPrev() {
        playerService.prevSong(this::refreshUI);
    }

    private void updateTimeLabel() {
        double current = playerService.getCurrentTimeSeconds();
        double total = playerService.getTotalTimeSeconds();
        timeLabel.setText(formatTime(current) + " / " + formatTime(total));
    }

    private void updateSeekSlider() {
        double current = playerService.getCurrentTimeSeconds();
        double total = playerService.getTotalTimeSeconds();
        if (total > 0) {
            int sliderVal = (int) ((current / total) * 100);
            seekSlider.setValue(sliderVal);
        }
    }

    private String formatTime(double seconds) {
        int hrs = (int) (seconds / 3600);
        int mins = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }

    /**
     * Updates button text, song label, etc. after a logic change
     */
    private void refreshUI() {
        // If playing -> Show 'Pause'
        // If paused -> Show 'Play'
        if (playerService.isPlaying()) {
            playPauseButton.setText("⏸ Pause");
        } else {
            playPauseButton.setText("▶ Play");
        }
        // Update current song label
        String songPath = playerService.getCurrentSongPath();
        if (songPath != null) {
            songLabel.setText("Now playing: " + new File(songPath).getName());
        } else {
            songLabel.setText(bundle.getString("mp3.not.loaded"));
        }
        // Update time and slider
        updateTimeLabel();
        updateSeekSlider();
        revalidate();
        repaint();
    }

    private void showPlayListPanel() {
        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Playlist Manager " + playerService.getCurrentSongPath(),
                true
        );
        PlayListManagerPanel playListPanel = new PlayListManagerPanel(playerService.getPlaylist().toString());
        dialog.getContentPane().add(playListPanel);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        // after closing, reload the playlist from the service
        playerService.loadPlaylist();
    }

    // --- SoundController interface methods ---
    @Override
    public void mute() {
        playerService.mute();
    }

    @Override
    public void unmute() {
        playerService.unmute();
    }

    @Override
    public boolean isMuted() {
        return playerService.isMuted();
    }
}
