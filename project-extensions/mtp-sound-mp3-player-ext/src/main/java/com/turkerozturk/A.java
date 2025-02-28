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

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class A extends JPanel {
    private MediaPlayer mediaPlayer;
    private List<String> playlist;

    private JButton playButton, pauseButton, stopButton, nextButton, prevButton;
    private JButton fastButton, slowButton; // Yeni hız düğmeleri
    private JLabel timeLabel; // Geçen Süre / Toplam Süre
    private JSlider seekSlider; // Müziği ileri / geri almak için slider

    public A() {
        setLayout(new BorderLayout());

        // Ana butonları oluştur
        playButton = new JButton("\u25B6");  // ▶ Play
        pauseButton = new JButton("\u23F8"); // ⏸ Pause
        stopButton = new JButton("\u25A0");  // ■ Stop
        nextButton = new JButton("\u23ED");  // ⏭ Next
        prevButton = new JButton("\u23EE");  // ⏮ Previous

        // Yeni Hız Düğmeleri
        fastButton = new JButton("1.5x");
        slowButton = new JButton("0.5x");

        // Zamanı gösterecek etiket
        timeLabel = new JLabel("00:00 / 00:00", SwingConstants.CENTER);

        // Seek Slider
        seekSlider = new JSlider(0, 100, 0);
        seekSlider.setPreferredSize(new Dimension(300, 20));

        // Panel ve Butonları ekle
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(fastButton);
        buttonPanel.add(slowButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(timeLabel, BorderLayout.CENTER);
        add(seekSlider, BorderLayout.SOUTH);

        // Event Listenerları Ekle
        addEventListeners();
    }

    private void addEventListeners() {
        playButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });

        pauseButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        stopButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        fastButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.setRate(1.5); // 1.5x hızında çalma
            }
        });

        slowButton.addActionListener(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.setRate(0.5); // 0.5x hızında yavaş çalma
            }
        });

        // Slider'ı güncelleme (Şarkı süresi değiştikçe kaydırma çubuğunu güncelle)
        if (mediaPlayer != null) {
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
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
        }
    }

    // Süreyi MM:SS formatına çeviren yardımcı metot
    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
