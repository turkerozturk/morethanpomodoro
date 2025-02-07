package org.example.jpanels.mp3;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Mp3PlayerFx extends JPanel {
    private MediaPlayer mediaPlayer;
    private List<String> playlist;
    private int currentSongIndex = 0;
    private float volume = 1.0f; // 0.0 - 1.0 arası
    private Thread playerThread;
    private boolean isPlaying = false;
    private final JFXPanel jfxPanel; // JavaFX başlatmak için

    private JButton playButton, stopButton, nextButton, prevButton;
    private JSlider volumeSlider;
    private JLabel songLabel;

    private String playlistFileLocation;

    public String getPlaylistFileLocation() {
        return playlistFileLocation;
    }

    public void setPlaylistFileLocation(String playlistFileLocation) {
        this.playlistFileLocation = playlistFileLocation;
    }

    public Mp3PlayerFx(String playlistFileLocation) {

        this.playlistFileLocation = playlistFileLocation;

        setLayout(new BorderLayout());

        // **ÖNEMLİ**: JavaFX Runtime'ı başlatmak için bir JFXPanel oluşturulmalı!
        jfxPanel = new JFXPanel(); // JavaFX Toolkit başlatır
        add(jfxPanel, BorderLayout.CENTER);

        // Playlist yükle
        loadPlaylist();

        // UI oluştur
        JPanel controlsPanel = new JPanel();
        playButton = new JButton("▶");
        stopButton = new JButton("■");
        nextButton = new JButton("⏭");
        prevButton = new JButton("⏮");
        volumeSlider = new JSlider(0, 100, 100);
        songLabel = new JLabel("Song not loaded");

        // Action Listeners
        playButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        nextButton.addActionListener(e -> nextSong());
        prevButton.addActionListener(e -> prevSong());
        volumeSlider.addChangeListener(e -> setVolume(volumeSlider.getValue()));

        // UI düzeni
        controlsPanel.add(prevButton);
        controlsPanel.add(playButton);
        controlsPanel.add(stopButton);
        controlsPanel.add(nextButton);
        controlsPanel.add(new JLabel("Sound Volume:"));
        controlsPanel.add(volumeSlider);

        add(controlsPanel, BorderLayout.SOUTH);
        add(songLabel, BorderLayout.NORTH);
    }

    private void loadPlaylist() {
        playlist = new ArrayList<>();
        File playlistFile = new File(playlistFileLocation);
        if (!playlistFile.exists()) {
            JOptionPane.showMessageDialog(this, playlistFile + " not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(playlistFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                playlist.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (isPlaying) return;

        if (playlist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Playlist empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        isPlaying = true;
        playerThread = new Thread(() -> {
            // **ÖNEMLİ**: JavaFX kodlarını `Platform.runLater()` içinde çalıştırmalıyız
            Platform.runLater(() -> playSong(currentSongIndex));
        });
        playerThread.start();
    }

    public void stop() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.stop());
            isPlaying = false;
        }
    }

    public void continuePlaying() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.play());
            isPlaying = true;
        }
    }

    public void setVolume(int volume) {
        float newVolume = volume / 100.0f;
        this.volume = newVolume;
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.setVolume(newVolume));
        }
    }

    public void nextSong() {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong(currentSongIndex);
    }

    public void prevSong() {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong(currentSongIndex);
    }

    public void playSong(int songIndex) {
        if (playlist.isEmpty()) return;

        currentSongIndex = songIndex % playlist.size();
        String songPath = playlist.get(currentSongIndex);
        songLabel.setText("Playing: " + new File(songPath).getName());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(new File(songPath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setOnEndOfMedia(this::nextSong);

        Platform.runLater(() -> mediaPlayer.play());
    }

    public JPanel getPlayerPanel() {
        return this;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MP3 Player FX");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            Mp3PlayerFx playerPanel = new Mp3PlayerFx("playlist1.txt");
            frame.add(playerPanel);
            frame.setVisible(true);
        });
    }
}
