package org.example.jpanels.mp3;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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

    private JButton playAndPauseAndContinueButton, stopButton, nextButton, prevButton;

    private JButton fastButton, slowButton; // Yeni hız düğmeleri
    private JLabel timeLabel; // Geçen Süre / Toplam Süre
    private JSlider seekSlider; // Müziği ileri / geri almak için slider
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

       // setLayout(new BorderLayout());

        // **ÖNEMLİ**: JavaFX Runtime'ı başlatmak için bir JFXPanel oluşturulmalı!
        jfxPanel = new JFXPanel(); // JavaFX Toolkit başlatır
        add(jfxPanel);

        // Playlist yükle
        loadPlaylist();

        // UI oluştur
        playAndPauseAndContinueButton = new JButton("\u25B6"); // ▶ (Play)
        stopButton = new JButton("\u25A0"); // ■ (Stop)
        nextButton = new JButton("\u23ED"); // ⏭ (Next)
        prevButton = new JButton("\u23EE"); // ⏮ (Previous)
        volumeSlider = new JSlider(0, 100, 100);
        songLabel = new JLabel("Song not loaded");

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
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS)); // Dikey hizalama

        verticalPanel.add(songLabel);


        JPanel timePanel = new JPanel();
        timePanel.add(seekSlider);
        timePanel.add(timeLabel);

        verticalPanel.add(timePanel);


        JPanel controlsPanel = new JPanel();

        controlsPanel.add(prevButton);
        controlsPanel.add(playAndPauseAndContinueButton);
        controlsPanel.add(stopButton);
        controlsPanel.add(nextButton);


        verticalPanel.add(controlsPanel);

        JPanel volumePanel = new JPanel();
        volumePanel.add(new JLabel("Sound Volume:"));
        volumePanel.add(volumeSlider);
        volumePanel.add(fastButton);
        volumePanel.add(slowButton);



        verticalPanel.add(volumePanel);




        //add(panel1);
        add(verticalPanel);

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

        if (mediaPlayer == null) {
            isPlaying = true;
            playerThread = new Thread(() -> {
                // **ÖNEMLİ**: JavaFX kodlarını `Platform.runLater()` içinde çalıştırmalıyız
                Platform.runLater(() -> playSong(currentSongIndex));

            });
            playerThread.start();

        }
        //if (isPlaying) return;
        if (mediaPlayer != null) {
            MediaPlayer.Status currentStatus = mediaPlayer.getStatus();

            if (currentStatus == null) {
                if (playlist.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Playlist empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                isPlaying = true;

                //System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());
                mediaPlayer.play();
               // playAndPauseAndContinueButton.setText("\u23F8"); // Pause (⏸)
            } else if (currentStatus == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                isPlaying = false;
                playAndPauseAndContinueButton.setText("\u23F8"); // Pause (⏸)

            } else if (currentStatus == MediaPlayer.Status.PAUSED || currentStatus == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
                playAndPauseAndContinueButton.setText("\u23F5"); // Continue (⏵)

                isPlaying = true;

                //System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());

            }

        }


    }

    public void stop() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.stop());
            isPlaying = false;
        }
    }

/*
    // https://stackoverflow.com/questions/38819690/javafx-media-pause-method-makes-mediaplayer-fast-forward
    public void playPause() {
        MediaPlayer.Status currentStatus = mediaPlayer.getStatus();

        if (currentStatus == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            pauseContinueButton.setText("\u23F5"); // Continue (⏵)

        } else if(currentStatus == MediaPlayer.Status.PAUSED || currentStatus == MediaPlayer.Status.STOPPED) {
            //System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());
            mediaPlayer.play();
            pauseContinueButton.setText("\u23F8"); // Pause (⏸)

        }



    }
*/

    /*
    public void continuePlaying() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.play());
            isPlaying = true;
        }
    }
    */

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
        System.out.println("time");
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
           // updateTimeLabel();
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MP3 Player FX");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            Mp3PlayerFx playerPanel = new Mp3PlayerFx("playlist1.txt");
            frame.add(playerPanel);
            frame.setVisible(true);
        });
    }
}
