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


import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mp3PlayerFxService {

    private MediaPlayer mediaPlayer;
    private List<String> playlist;
    private int currentSongIndex = 0;
    private float volume = 1.0f;       // range: 0.0 - 1.0
    private boolean isMuted = false;
    private boolean isPlaying = false;

    private final String playlistFileLocation;

    public Mp3PlayerFxService(String playlistFileLocation) {
        this.playlistFileLocation = playlistFileLocation;
        loadPlaylist();
    }

    /**
     * Loads the playlist from the specified file.
     */
    public void loadPlaylist() {
        playlist = new ArrayList<>();
        File playlistFile = new File(playlistFileLocation);
        if (playlistFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(playlistFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    playlist.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts or toggles playback.
     */
    public void startPlayback(Runnable updateUI) {
        if (playlist.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Playlist is empty. Cannot start playback.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If there's no mediaPlayer, create it
        if (mediaPlayer == null) {
            isPlaying = true;
            // Must run JavaFX code in the JavaFX thread
            Platform.runLater(() -> playSong(currentSongIndex, updateUI));
            return;
        }

        // If mediaPlayer exists, check its status
        MediaPlayer.Status currentStatus = mediaPlayer.getStatus();
        if (currentStatus == MediaPlayer.Status.PLAYING) {
            // Pause
            mediaPlayer.pause();
            isPlaying = false;
        } else if (currentStatus == MediaPlayer.Status.PAUSED
                || currentStatus == MediaPlayer.Status.STOPPED
                || currentStatus == null) {
            // Continue
            mediaPlayer.play();
            isPlaying = true;
        }

        // Trigger UI refresh (button text, time label, etc.)
        if (updateUI != null) {
            updateUI.run();
        }
    }

    /**
     * Stops playback and resets to the beginning.
     */
    public void stopPlayback(Runnable updateUI) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.seconds(0));
            isPlaying = false;
        }
        if (updateUI != null) {
            updateUI.run();
        }
    }

    /**
     * Play the next song in the playlist.
     */
    public void nextSong(Runnable updateUI) {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong(currentSongIndex, updateUI);
    }

    /**
     * Play the previous song in the playlist.
     */
    public void prevSong(Runnable updateUI) {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong(currentSongIndex, updateUI);
    }

    /**
     * Actually create a MediaPlayer for the given index and start playing.
     */
    public void playSong(int songIndex, Runnable updateUI) {
        if (playlist.isEmpty()) return;

        currentSongIndex = songIndex % playlist.size();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        String songPath = playlist.get(currentSongIndex);
        Media media = new Media(new File(songPath).toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);

        // Set volume (mute if needed)
        if (isMuted) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(volume);
        }

        mediaPlayer.setOnEndOfMedia(() -> nextSong(updateUI));
        mediaPlayer.play();

        isPlaying = true;
        if (updateUI != null) {
            updateUI.run();
        }
    }

    public void setVolume(int newVolume) {
        this.volume = newVolume / 100.0f;
        if (mediaPlayer != null && !isMuted) {
            Platform.runLater(() -> mediaPlayer.setVolume(volume));
        }
    }

    public double getCurrentTimeSeconds() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0.0;
    }

    public double getTotalTimeSeconds() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            return mediaPlayer.getTotalDuration().toSeconds();
        }
        return 0.0;
    }

    public void seekToPosition(double percent) {
        if (mediaPlayer != null) {
            double totalSec = getTotalTimeSeconds();
            double seekTime = percent * totalSec;
            mediaPlayer.seek(Duration.seconds(seekTime));
        }
    }

    /**
     * Set playback rate, e.g., 1.5x or 0.5x
     */
    public void setPlaybackRate(double rate) {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(rate);
        }
    }

    // --- Mute / Unmute handling ---
    public void mute() {
        if (mediaPlayer != null) {
            isMuted = true;
            Platform.runLater(() -> mediaPlayer.setVolume(0));
        }
    }

    public void unmute() {
        if (mediaPlayer != null) {
            isMuted = false;
            Platform.runLater(() -> mediaPlayer.setVolume(volume));
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    // --- Getters / State checks ---
    public boolean isPlaying() {
        return isPlaying;
    }

    public String getCurrentSongPath() {
        if (playlist.isEmpty()) return null;
        return playlist.get(currentSongIndex);
    }

    public List<String> getPlaylist() {
        return playlist;
    }
}
