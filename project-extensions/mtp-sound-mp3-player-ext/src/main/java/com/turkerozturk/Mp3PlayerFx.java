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
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;
import com.turkerozturk.equaliser.EqualiserPanel;
import com.turkerozturk.playlist.PlayListManagerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mp3PlayerFx extends JPanel implements SoundController {

    Timer timer; // gecen sureyi saniyede bir gostermesi icin.
    private MediaPlayer mediaPlayer;
    private List<String> playlist;
    private int currentSongIndex = 0;
    private float volume = 1.0f; // 0.0 - 1.0 arası
    private Thread playerThread;
    private boolean isPlaying = false;
    private final JFXPanel jfxPanel; // JavaFX başlatmak için

    private JButton playAndPauseAndContinueButton, stopButton, nextButton, prevButton, fastButton, slowButton,
            playListButton;

    private JLabel timeLabel; // Geçen Süre / Toplam Süre
    private JSlider seekSlider; // Müziği ileri / geri almak için slider
    private JSlider volumeSlider;
    private JLabel songLabel;

    private String playlistFileLocation;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();


    public String getPlaylistFileLocation() {
        return playlistFileLocation;
    }

    public void setPlaylistFileLocation(String playlistFileLocation) {
        this.playlistFileLocation = playlistFileLocation;
    }

    public Mp3PlayerFx(String playlistFileLocation) {

        this.playlistFileLocation = playlistFileLocation;

       // setLayout(new BorderLayout());

        // **ÖNEMLİ**: JavaFX Runtime'ı başlatmak için bir JFXPanel oluşturulmalı!
        jfxPanel = new JFXPanel(); // JavaFX Toolkit başlatır
        add(jfxPanel);

        // Playlist yükle
        loadPlaylist();


        playListButton = new JButton("Manage Playlist");
        playListButton.addActionListener(e -> showPlayListPanel());

        // UI oluştur
        playAndPauseAndContinueButton = new JButton("▶ " + "Play"); // ▶ (Play)
        stopButton = new JButton("■ "+ "Stop"); // ■ (Stop)
        nextButton = new JButton("⏭ " + "Next"); // ⏭ (Next)
        prevButton = new JButton( "⏮ " + "Previous"); // ⏮ (Previous)
        volumeSlider = new JSlider(0, 100, 100);
        songLabel = new JLabel(bundle.getString("mp3.not.loaded"));
        if(!playlist.isEmpty()) {
            String songPath = playlist.get(0);
            songLabel.setText(songPath);
        }


        // Action Listeners
        playAndPauseAndContinueButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        nextButton.addActionListener(e -> nextSong());
        prevButton.addActionListener(e -> prevSong());
        volumeSlider.addChangeListener(e -> setVolume(volumeSlider.getValue()));



        // Yeni Hız Düğmeleri
        fastButton = new JButton("1.5x");
        slowButton = new JButton("0.5x");
        // Zamanı gösterecek etiket
        timeLabel = new JLabel("00:00 / 00:00", SwingConstants.CENTER);
        // Seek Slider
        seekSlider = new JSlider(0, 100, 0);
        seekSlider.setPreferredSize(new Dimension(300, 20));

        fastButton.addActionListener(e -> fast());
        slowButton.addActionListener(e -> slow());

        JPanel verticalPanel = new JPanel();
       // JPanel panel = new JPanel();
        verticalPanel.setLayout(new GridLayout(10, 1, 5, 5));
        // Ust tarafta 20 piksel bsşluk birak
        verticalPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        verticalPanel.add(playListButton, BorderLayout.CENTER);


        JPanel timePanel = new JPanel();
        timePanel.add(seekSlider);

        timePanel.add(timeLabel);

        verticalPanel.add(timePanel);

        verticalPanel.add(songLabel);


        JPanel controlsPanel = new JPanel();

        controlsPanel.add(prevButton);
        controlsPanel.add(playAndPauseAndContinueButton);
        controlsPanel.add(stopButton);
        controlsPanel.add(nextButton);


        verticalPanel.add(controlsPanel);

        JPanel volumePanel = new JPanel();
        volumePanel.add(new JLabel(bundle.getString("mp3.sound.volume")));
        volumePanel.add(volumeSlider);
        volumePanel.add(fastButton);
        volumePanel.add(slowButton);



        verticalPanel.add(volumePanel, BorderLayout.CENTER);


        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(verticalPanel);

        //add(panel1);
        add(wrapperPanel);

        // 1 saniyede bir tetiklenecek timer yarat
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // timeLabel'i yeni süre bilgisiyle güncelle
                double currentTimeSeconds = mediaPlayer.getCurrentTime().toSeconds();
                System.out.println(currentTimeSeconds);
                timeLabel.setText(formatTime(Duration.seconds(currentTimeSeconds)));
            }
        });

        addEventListeners();

    }

    private void fast() {
        fastButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.setRate(1.5); // 1.5x hızında çalma
                mediaPlayer.play();

            }
        });
    }

    private void slow() {
        fastButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.setRate(0.5); // 1.5x hızında çalma
                mediaPlayer.play();
            }
        });
    }

    private void loadPlaylist() {
        playlist = new ArrayList<>();
        File playlistFile = new File(playlistFileLocation);
        /*
        if (!playlistFile.exists()) {
            JOptionPane.showMessageDialog(this, playlistFile
                    + bundle.getString("mp3.not.found"), bundle.getString("mp3.error")
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }
        */
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

    public void start() {

        if (mediaPlayer == null) {
            isPlaying = true;
            playerThread = new Thread(() -> {
                // bilgi: **ÖNEMLİ**: JavaFX kodlarını `Platform.runLater()` içinde çalıştırmalıyız
                Platform.runLater(() -> playSong(currentSongIndex));

            });
            playerThread.start();

        }
        //if (isPlaying) return;
        if (mediaPlayer != null) {
            MediaPlayer.Status currentStatus = mediaPlayer.getStatus();

            if (currentStatus == null) {
                if (playlist.isEmpty()) {
                    JOptionPane.showMessageDialog(this, bundle.getString("mp3.playlist.empty")
                            , bundle.getString("mp3.error")
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());
                mediaPlayer.play();
                isPlaying = true;

                timer.start();
               // playAndPauseAndContinueButton.setText("\u23F8"); // Pause (⏸)
            } else if (currentStatus == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                isPlaying = false;
                playAndPauseAndContinueButton.setText("⏸ " + "Pause"); // Pause (⏸)
                timer.stop();

            } else if (currentStatus == MediaPlayer.Status.PAUSED || currentStatus == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
                playAndPauseAndContinueButton.setText("▶ " + "Play"); // Continue (⏵)

                isPlaying = true;
                timer.start();
                //System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());

            }

        }

        // baslangicta mp3 toplam suresini dogru gostermesi icin gecikme suresi.
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        updateTimeLabel();
        updateSeekSlider();

    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(0));
            seekSlider.setValue(0);
            Platform.runLater(() -> mediaPlayer.stop());
            isPlaying = false;
            updateTimeLabel();
            updateSeekSlider();

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
        songLabel.setText(bundle.getString("mp3.now.playing") + new File(songPath).getName());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(new File(songPath).toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setOnEndOfMedia(this::nextSong);

        Platform.runLater(() -> {

            mediaPlayer.play();
            // Ekolayzer Toggle butonu
            JButton toggleEqualizerButton = new JButton("Equalizer");
            toggleEqualizerButton.addActionListener(e -> toggleEqualizer());

            add(toggleEqualizerButton, BorderLayout.SOUTH);
            revalidate();
            repaint();
        });

        updateTimeLabel();
        updateSeekSlider();

    }

    public JPanel getPlayerPanel() {
        return this;
    }


    private void addEventListeners() {

        // Slider'ı güncelleme (Şarkı süresi değiştikçe kaydırma çubuğunu güncelle)
        if (mediaPlayer != null) {
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                //   durationSlider.setValue(newTime.toMillis() / mp.getTotalDuration().toMillis() * 100);
                updateTimeLabel();
                updateSeekSlider();
            });
        }

        // Kullanıcı sürükleyerek süreyi değiştirdiğinde
        seekSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                seekToPosition();
            }
        });

        seekSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                seekToPosition();
            }
        });





    }

        // Süreyi etikete güncelleyen metot
    private void updateTimeLabel() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalTime = mediaPlayer.getTotalDuration();

            timeLabel.setText(formatTime(currentTime) + " / " + formatTime(totalTime));
        }
    }

    // Seek barı güncelleyen metot
    private void updateSeekSlider() {
        if (mediaPlayer != null) {
            double currentTimeSeconds = mediaPlayer.getCurrentTime().toSeconds();
            double totalTimeSeconds = mediaPlayer.getTotalDuration().toSeconds();

            if (totalTimeSeconds > 0) {
                int sliderValue = (int) ((currentTimeSeconds / totalTimeSeconds) * 100);
                seekSlider.setValue(sliderValue);
            }
        }
    }

    // Kullanıcı seekSlider'ı değiştirdiğinde şarkıyı ileri/geri al
    private void seekToPosition() {
        if (mediaPlayer != null) {
            double sliderValue = seekSlider.getValue();
            double totalTimeSeconds = mediaPlayer.getTotalDuration().toSeconds();
            double seekTime = (sliderValue / 100.0) * totalTimeSeconds;

            mediaPlayer.seek(Duration.seconds(seekTime));
            updateTimeLabel();

        }
    }

    // Süreyi MM:SS formatına çeviren yardımcı metot
    private String formatTime(Duration duration) {
        int hours = (int) duration.toHours();
        int minutes = (int) duration.toMinutes() % 60;
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void showPlayListPanel() {
        // Yeni bir JDialog oluştur (parent frame’i modal yapar)
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Playlist Manager " + playlistFileLocation, true);

        // PlayListPanel'i oluştur ve JDialog içine ekle
        PlayListManagerPanel playListPanel = new PlayListManagerPanel(playlistFileLocation);
        dialog.getContentPane().add(playListPanel);

        // JDialog özellikleri
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this); // Parent'a göre ortala
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Kapatınca hafızadan silinsin

        // Görüntüle
        dialog.setVisible(true);

        // Seçili playlist değerini al
        String selectedPlaylistItem = playListPanel.getSelectedPlaylistItem();

        if (selectedPlaylistItem != null) {
            System.out.println("Seçilen Playlist: " + selectedPlaylistItem); // Parent pencereye iletebilirsin
        }



        loadPlaylist();





    }


    @Override
    public void mute() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.setVolume(0));
        }
    }

    @Override
    public void unmute() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.setVolume(50));
        }
    }

    @Override
    public boolean isMuted() {
        return false;
    }


    private JFrame equalizerFrame;
    private EqualiserPanel equalizerPanel;
    private boolean isEqualizerEnabled = true;
    // Ekolayzeri açıp kapatan metot
    private void toggleEqualizer() {
        if (equalizerFrame == null) {
            equalizerFrame = new JFrame("Equalizer");
            equalizerPanel = new EqualiserPanel(mediaPlayer);

            // Ekolayzer Enable/Disable Toggle Butonu
            JToggleButton enableDisableToggle = new JToggleButton("Enable Equalizer", isEqualizerEnabled);
            enableDisableToggle.addActionListener(e -> {
                isEqualizerEnabled = enableDisableToggle.isSelected();
                equalizerPanel.setEnabled(isEqualizerEnabled);
            });

            equalizerFrame.setLayout(new BorderLayout());
            equalizerFrame.add(equalizerPanel, BorderLayout.CENTER);
            equalizerFrame.add(enableDisableToggle, BorderLayout.SOUTH);
            equalizerFrame.setSize(400, 300);
            equalizerFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }

        equalizerFrame.setVisible(!equalizerFrame.isVisible());
    }



}

