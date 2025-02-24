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
package org.example;

import org.example.PanelPlugin;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CountdownTimerPanel extends JPanel implements PanelPlugin {


    private JSpinner minuteSpinner;
    private JLabel countdownLabel;
    private JButton startButton;

    private JCheckBox displayMessageCheck;
    private JCheckBox playWaveCheck;
    private JCheckBox runProgramCheck;

    private JTextField waveFileField;
    private JTextField programField;

    private Timer timer;
    private int remainingSeconds;

    public CountdownTimerPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1) Dakika giriş alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Minutes: "), gbc);

        minuteSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 999, 1));
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(minuteSpinner, gbc);

        // 2) Geri sayım etiketi (büyük yazı)
        countdownLabel = new JLabel("00:00", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 48));
        countdownLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(countdownLabel, gbc);

        // 3) Aksiyon seçenekleri
        displayMessageCheck = new JCheckBox("Display message");
        displayMessageCheck.setSelected(true);

        playWaveCheck = new JCheckBox("Play wave file");
        waveFileField = new JTextField(".\\sounds\\alarm_clock_bell.wav", 15);

        runProgramCheck = new JCheckBox("Run program");
        programField = new JTextField("notepad.exe", 15); // örnek olarak notepad

        // Display message
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(displayMessageCheck, gbc);

        // Play wave
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(playWaveCheck, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(waveFileField, gbc);

        // Run program
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(runProgramCheck, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(programField, gbc);

        // 4) Başlat butonu
        startButton = new JButton("Start");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(startButton, gbc);

        // Butona basılınca timer başlasın
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCountdown();
            }
        });
    }

    private void startCountdown() {
        // Önce varsa eski timer'ı durdur
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        // Spinner'dan dakika değerini al
        int minutes = (int) minuteSpinner.getValue();
        remainingSeconds = minutes * 60;

        // Timer oluştur (1 saniyede bir çalışacak)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                updateCountdownLabel();
                if (remainingSeconds <= 0) {
                    // Sayaç bitti
                    timer.stop();
                    doActionsWhenFinished();
                }
            }
        });

        // Timer'ı başlat
        timer.start();
        updateCountdownLabel();
    }

    private void updateCountdownLabel() {
        // mm:ss formatı
        int min = remainingSeconds / 60;
        int sec = remainingSeconds % 60;
        String timeText = String.format("%02d:%02d", min, sec);
        countdownLabel.setText(timeText);
    }

    private void doActionsWhenFinished() {
        // 1) Display message seçili ise
        if (displayMessageCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, "Time's up!");
        }

        // 2) Play wave file seçili ise
        if (playWaveCheck.isSelected()) {
            String filePath = waveFileField.getText().trim();
            playWaveFile(filePath);
        }

        // 3) Run program seçili ise
        if (runProgramCheck.isSelected()) {
            String command = programField.getText().trim();
            runProgram(command);
        }
    }

    private void playWaveFile(String filePath) {
        // Yeni bir thread'de ses çal
        new Thread(() -> {
            try {
                File audioFile = new File(filePath);
                if (!audioFile.exists()) {
                    System.err.println("Wave file not found: " + filePath);
                    return;
                }
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runProgram(String command) {
        // Komutu işletim sisteminde çalıştır
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Basit bir test penceresi
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Countdown Timer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CountdownTimerPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.countdown.timer.title";
    }

    @Override
    public JPanel getPanel() {
        return new CountdownTimerPanel();
    }
}
