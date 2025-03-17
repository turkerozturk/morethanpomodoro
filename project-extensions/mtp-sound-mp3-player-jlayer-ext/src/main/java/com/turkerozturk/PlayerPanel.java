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

import com.iheartradio.m3u8.data.TrackData;
import com.turkerozturk.chatgpt.M3U8PlaylistManager;
import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;
import com.turkerozturk.mp3info.Mp3Info;
import com.turkerozturk.playlist.PlayListManagerPanel;
import com.turkerozturk.playlist.PlayListSelectionListener;
import com.turkerozturk.thirdparty.SeekBar;
import javazoom.jlgui.basicplayer.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//public class PlayerPanel extends JPanel implements PanelPlugin, SoundController, PlayListSelectionListener, BasicPlayerListener {
public class PlayerPanel extends JPanel implements PanelPlugin, SoundController, PlayListSelectionListener {

    // Instantiate BasicPlayer.
    BasicPlayer player = new BasicPlayer();
    // BasicPlayer is a BasicController.
    BasicController control = (BasicController) player;

    Map<Object, Object> propertiesOfAudioFile = new HashMap<>();


    private M3U8PlaylistManager m3U8PlaylistManager = M3U8PlaylistManager.getInstance();

    private TrackData currenTrackData;
    //  private AudioPlayerTurker player = AudioPlayerTurker.getInstance();

    //private ArrayList<String> songList = new ArrayList<>();

    private float currentAudioDurationSec = 0;

    private SeekBar seekbar = new SeekBar();

    double initialVolume = 30; // TODO config.properties

    private int eventCode;


    float volume;


    // JToggleButton nesneleri
    private JToggleButton fileInfoToggle;
    private JToggleButton playListToggle;
    private JToggleButton repeatToggle;
    private JToggleButton playToggle;
    private JToggleButton muteToggle;
    private JToggleButton equaliserToggle;

    // JButton nesneleri
    private JButton previousButton;
    private JButton nextButton;
    private JButton stopButton;

    // JSlider nesneleri
    //private JSlider seekSlider;
    private JSlider volumeSlider;

    // JLabel nesneleri
    private JLabel fileInfoLabel;
    private JLabel fileNameLabel;
    private JLabel currentDurationLabel;
    private JLabel speedLabel;
    private JLabel totalDurationLabel;
    private String playListFilePath;// = "C:\\tmp\\mp3samples\\playlist1.m3u8";


    // Pencere son kapatıldığında PlayList penceresinin konumu neredeydi?
    // Burada saklıyoruz ki tekrar açıldığında aynı konumda olsun.
    private Point lastPlayListPanelLocation = null;

    // playlist penceresi Kullanıcı penceresini henüz kendi başına taşımazsa, ana pencereyi “takip etsin” mi?
    private boolean followMainWindow = true;

    PlayListManagerPanel playListManagerPanel;


    public PlayerPanel() {


        // Register BasicPlayerTest to BasicPlayerListener events.
        // It means that this object will be notified on BasicPlayer
        // events such as : opened(...), progress(...), stateUpdated(...)
        //player.addBasicPlayerListener(this);


        // Paneli dikeyde üç ana satıra bölmek için BoxLayout kullanıyoruz
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // setBackground(Color.RED);

        // --- 1. Satır (üst kısım) --------------------------------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // topPanel.setBackground(Color.WHITE);

        fileInfoToggle = new JToggleButton("FileInfo");
        fileInfoToggle.setEnabled(false); // henuz acik bir dosya yok. opened isimli listenerle enable ediyoruz sonradan.

        playListToggle = new JToggleButton("Playlist");

        fileNameLabel = new JLabel("Filename");
        // Örnek boş listener
        fileInfoToggle.addActionListener(e -> {
            // Şimdilik boş
            //System.out.println("duration: " + player.getAudioDurationSeconds());
            //System.out.println("fileInfoToggle.addActionListener: " + m3U8PlaylistManager.getCurrentTrack().getUri());
            showAudioFileInfo();
        });


        //PlayListManagerPanel playListPanel = new PlayListManagerPanel(playerService.getPlaylist().toString());
        //System.out.println("PlayerPanel.java: " + playListFilePath);

        if (playListFilePath != null && Files.exists(Paths.get(playListFilePath))) {
            m3U8PlaylistManager.setCurrentPlaylistPath(playListFilePath);
            try {
                m3U8PlaylistManager.openPlaylist();


            } catch (Exception e) {
                // throw new RuntimeException(e);
                System.out.println("Playlist is corrupt: " + playListFilePath);
            }
        } else {
            m3U8PlaylistManager.newPlaylist();
        }

        playListManagerPanel = new PlayListManagerPanel();
        playListManagerPanel.setPlayListSelectionListener(this); // inteface aracigiliyla playlistteki click eventlerini almak icin.

        //playListPanel.setPlaylistList("");
        if (m3U8PlaylistManager.getTrackList().isEmpty()) {
            fileNameLabel.setText("Playlist is empty. Add Tracks To Playlist!");
        } else {


            // program calistiginda bir playlist varsa ilk sarkiyi secer.
            currenTrackData = m3U8PlaylistManager.getTrackList().get(0);
            m3U8PlaylistManager.setCurrentTrack(currenTrackData);
            m3U8PlaylistManager.setCurrentTrackIndex(0);

            playListManagerPanel.getPlaylistList().setSelectedIndex(m3U8PlaylistManager.getCurrentTrackIndex());

            try {
                player.open(new File(currenTrackData.getUri()));
            } catch (BasicPlayerException e) {
                throw new RuntimeException(e);
            }

            if (currenTrackData.getTrackInfo() != null) {
                fileNameLabel.setText(currenTrackData.getTrackInfo().title);
            } else {
                fileNameLabel.setText(currenTrackData.getUri());
            }

        }


        playListToggle.addActionListener(e -> {
            if (playListToggle.isSelected()) {
                showPlayListPanel();
                playListToggle.setSelected(true);
            } else {

                //playListManagerDialog.dispose();
                playListManagerDialog.setVisible(false);
                playListToggle.setSelected(false);
            }
        });

        topPanel.add(fileInfoToggle);
        topPanel.add(playListToggle);
        topPanel.add(fileNameLabel);


        // --- 2. Satır (orta kısım) -------------------------------------------
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // middlePanel.setBackground(Color.BLUE);

        currentDurationLabel = new JLabel("CurrentDuration");
        speedLabel = new JLabel("Speed"); // "Slow/Fast" ile ilgili bir etiket
        //seekSlider = new JSlider();
        totalDurationLabel = new JLabel("TotalDuration");

        // Varsayılan boş listener örneği
        //seekSlider.addChangeListener(e -> {
        // Şimdilik boş
        //});

        middlePanel.add(currentDurationLabel);
        middlePanel.add(speedLabel);
        //middlePanel.add(seekSlider);
        seekbar.setPreferredSize(new Dimension(300, 10));
        middlePanel.add(seekbar);
        middlePanel.add(totalDurationLabel);

        // --- 3. Satır (alt kısım) --------------------------------------------
        // Alt kısım iki yana bölünüyor: Sol taraf (Repeat, Previous, Play, Next, Stop),
        // Sağ taraf (Mute, VolumeSlider, Equaliser)

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 5));
        //bottomPanel.setBackground(Color.YELLOW);

        // Sol alt panel
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        //bottomLeftPanel.setBackground(Color.YELLOW);

        repeatToggle = new JToggleButton("Repeat");
        previousButton = new JButton("Previous");
        playToggle = new JToggleButton("Play");
        nextButton = new JButton("Next");
        stopButton = new JButton("Stop");

        // Boş action listener örnekleri
        repeatToggle.addActionListener(e -> {
        });
        previousButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                TrackData trackData = m3U8PlaylistManager.previousTrack();
                if (trackData != null) {
                    currenTrackData = trackData;
                    //System.out.println(currenTrackData.getUri());
                    fileNameLabel.setText(currenTrackData.getUri());
                    playListManagerPanel.getPlaylistList().setSelectedIndex(m3U8PlaylistManager.getCurrentTrackIndex());
                    try {
                        boolean continueToPlay = PlayerEvent.fromEventId(eventCode).equals(PlayerEvent.PLAYING);
                        player.stop();
                        player.open(new File(currenTrackData.getUri()));
                        if (continueToPlay) {
                            player.play();
                        }
                    } catch (BasicPlayerException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        playToggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    tooglePlay();
                } catch (BasicPlayerException e1) {
                    e1.printStackTrace();
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                TrackData trackData = m3U8PlaylistManager.nextTrack();
                if (trackData != null) {
                    currenTrackData = trackData;
                    fileNameLabel.setText(currenTrackData.getUri());
                    playListManagerPanel.getPlaylistList().setSelectedIndex(m3U8PlaylistManager.getCurrentTrackIndex());
                    try {
                        boolean continueToPlay = PlayerEvent.fromEventId(eventCode).equals(PlayerEvent.PLAYING);
                        player.stop();
                        player.open(new File(currenTrackData.getUri()));
                        if (continueToPlay) {
                            player.play();
                        }
                    } catch (BasicPlayerException e) {
                        //throw new RuntimeException(e);
                        e.printStackTrace();
                    }
                }
            }
        });

        stopButton.addActionListener(e -> {
            try {
                player.stop();
                playToggle.setSelected(false);
                playToggle.setText("Play");
            } catch (BasicPlayerException ex) {
                throw new RuntimeException(ex);
            }
        });

        // TODO bottomLeftPanel.add(repeatToggle);
        bottomLeftPanel.add(previousButton);
        bottomLeftPanel.add(playToggle);
        bottomLeftPanel.add(nextButton);
        bottomLeftPanel.add(stopButton);

        // Sağ alt panel
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        // bottomRightPanel.setBackground(Color.GREEN);

        muteToggle = new JToggleButton("Mute");
        volumeSlider = new JSlider();
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) initialVolume);

        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBorder(
                BorderFactory.createTitledBorder("Volume"));
        volumeSlider.setValue((int) initialVolume);
        equaliserToggle = new JToggleButton("Equaliser");

        // Boş action listener örnekleri
        muteToggle.addActionListener(e -> {

            if (volume != 0) {
                setVolume(0.0f);
                volume = 0;
            } else {
                volume = ((float) volumeSlider.getValue()) / 100;
                setVolume(volume);
            }
        });
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                triggerVolume();
            }
        });
        equaliserToggle.addActionListener(e -> {
        });

        bottomRightPanel.add(muteToggle);
        bottomRightPanel.add(volumeSlider);
        // TODO bottomRightPanel.add(equaliserToggle);

        bottomPanel.add(bottomLeftPanel);
        bottomPanel.add(bottomRightPanel);

        // --- Ek bilgi (isteğe bağlı): FileInfo Label eklemek isterseniz -------
        // Sorudaki "JLabel olanlar: FileInfo" ifadesi nedeniyle ekstra label:
        fileInfoLabel = new JLabel("FileInfo Label");
        topPanel.add(fileInfoLabel);

        // fileInfoLabel.setForeground(Color.BLUE);
        // Bu label'ı en üst panele eklemek veya başka yere koymak isterseniz:
        // topPanel.add(fileInfoLabel);  // Örnek

        // --- Panelleri ana panele ekle ---------------------------------------
        add(topPanel);
        add(middlePanel);
        add(bottomPanel);


        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimers();  // UI güncellemeleri için uygundur
                updatePlayingText();
            }
        });
        timer.start();


//Player related behaviour
        player.addBasicPlayerListener(new BasicPlayerListener() {

            @Override
            public void stateUpdated(BasicPlayerEvent event) {

                if (event.getCode() == BasicPlayerEvent.EOM) {
                    lastSeekMs = 0;
                    // BASLA EOM durumunda sonraki ses dosyasını çalmaya devam eder
                    // TODO
                    try {
                        m3U8PlaylistManager.nextTrack(); // turker
                        player.open(new File(m3U8PlaylistManager.getCurrentTrack().getUri()));
                        player.play();
                    } catch (BasicPlayerException e) {
                        throw new RuntimeException(e);
                    }
                    // BITTI EOM durumunda sonraki ses dosyasını çalmaya devam eder

                    //  paused = true; //reset player state
                    //  opened = false;
                    //  log("EOM event catched, player resetted.");
                }


                // bilgi: COK ONEMLİ
                // BasicPlayerEvent classina control click yaparsan toString() metodunun olabilecek butun eventleri
                // akillica konsola yazdirdigini gorursun. Bu stateUpdated interface metodu sayesinde surekli olarak
                // o anki audio durumu hakkinda info gelmis oluyor.
                //System.out.println(event.toString());
                fileInfoLabel.setText(event.toString());

                eventCode = event.getCode();


                StringBuilder s = new StringBuilder();

                s.append(String.format("code(%d): %s", eventCode, PlayerEvent.fromEventId(eventCode)));


                s.append("\tpos:");
                s.append(event.getPosition());

                s.append("\tval:");
                s.append(event.getValue());

                s.append("\tdesc: ");
                s.append(event.getDescription());

                s.append("\tsrc:");
                s.append(event.getSource());

                System.out.println(s);


                if (event.getCode() == BasicPlayerEvent.UNKNOWN) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.OPENING) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.OPENED) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.PLAYING) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.STOPPED) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.PAUSED) {
                    // btnPlay.setIcon(playIcon);
                    //playToggle.setText("Resume");
                }

                if (event.getCode() == BasicPlayerEvent.RESUMED) {
                    //  btnPlay.setIcon(pauseIcon);
                    //playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.SEEKING) {
                    isSeeking = true;
                }

                if (event.getCode() == BasicPlayerEvent.SEEKED) {
                    isSeeking = false;
                }


                if (event.getCode() == BasicPlayerEvent.EOM) {
                    //  seekbar.resetLastSeek();

                    // TODO
                        /*
                        if (!repeatToggle.isSelected()) {
                            //    player.nextSong();
                        } else {
                            player.play();
                        }
                        */
                    //  log("EOM event catched, calling next song.");
                }

                if (event.getCode() == BasicPlayerEvent.PAN) {
                    //  btnPlay.setIcon(pauseIcon);
                    // playToggle.setText("Pause");
                }

                if (event.getCode() == BasicPlayerEvent.GAIN) {
                    //  btnPlay.setIcon(pauseIcon);
                    // playToggle.setText("Pause");
                }


            }

            @Override
            public void setController(BasicController arg0) {
            }


            @Override
            public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {

                //propertiesOfAudioFile = properties;

                //we don't want to use microseconds directly because it gets resetted on seeking
                //System.out.println("ms: " + getProgressMicroseconds() + "\tdur:" + audioDurationInSeconds + "\teventCode: " + PlayerEvent.fromEventId(eventCode));
                seekbar.updateSeekBar(getProgressMicroseconds(), audioDurationInSeconds);

                csms = microseconds;
                cpcmdata = pcmdata;
                 /*
                if (wff != null) {
                    wff.updateWave(pcmdata);
                }
                if (fdf != null) {
                    fdf.updateWave(pcmdata);
                }
                */
            }


            /**
             * dosya açıldığında play ile çalmasak bile bu metod çalışır yani open durumu gerçekleşir.
             */
            @Override
            public void opened(Object stream, Map properties) {

                Object[] e = properties.entrySet().toArray();
                Object[] k = properties.keySet().toArray();
                for (int i = 0; i < properties.size(); i++) {
                    propertiesOfAudioFile.put(k[i], e[i]);

                }

                File file = new File(m3U8PlaylistManager.getCurrentTrack().getUri());
                long audioFileLength = file.length();
                int frameSize = (int) properties.get("mp3.framesize.bytes");
                float frameRate = (float) properties.get("mp3.framerate.fps");
                audioFrameSize = frameSize;
                audioFrameRate = frameRate;
                audioDurationInSeconds = (audioFileLength / (frameSize * frameRate));
            }
        });


        //  volumeSlider.setValue((int) volume);


    }


    private void showAudioFileInfo() {

        // TODO ayni audio dosyasi icin ikinci kez dugmeye basilirsa bekletmemesi icin ilk yuklemeden sonra
        //  baska degiskende tutmak mantikli olabilir.
        if (!propertiesOfAudioFile.isEmpty()) {

            String fileName = m3U8PlaylistManager.getCurrentTrack().getUri();
            File file = new File(fileName);

            // player.open demeye gerek yok cunku buraya gelmeden once open edilmis durumda oluyor.
        /*
                try {
                    player.open(file);
                } catch (BasicPlayerException e) {
                    throw new RuntimeException(e);
                }
                */
            //Map<Object, Object> audioPropertiesMap = player.getAudioPropertiesMap();
            //   if (audioPropertiesMap.size() > 0) {
            //String fileName = player.getPlaylist().get(player.getIndexSong());


            //long audioFileLength = file.length(); // TODO dosya boyutu
            //int frameSize = (int) audioPropertiesMap.get("mp3.framesize.bytes");
            //float frameRate = (float) audioPropertiesMap.get("mp3.framerate.fps");
            //int audioDurationInSeconds = (int) (audioFileLength / (frameSize * frameRate));
            propertiesOfAudioFile.put("FILENAME", fileName);
            // if(audioPropertiesMap.containsKey("duration")) {

            //   long durationMicroseconds = Long.parseLong(propertiesOfAudioFile.get("duration").toString());
            //   int audioDurationInSeconds = (int) TimeUnit.MICROSECONDS.toSeconds(durationMicroseconds);
// HH:MM:SS formatına çevir
            //     long hours = audioDurationInSeconds / 3600;
            //   long minutes = (audioDurationInSeconds % 3600) / 60;
            //    long seconds = audioDurationInSeconds % 60;


            //     propertiesOfAudioFile.put("DURATION(seconds)", audioDurationInSeconds);

            //    String durationHHMMSS = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            //     propertiesOfAudioFile.put("DURATION(HH:MM:SS)", durationHHMMSS);


            int durationSec = (int) MP3DurationUtils.getDurationInSeconds(propertiesOfAudioFile);
            String formatted = MP3DurationUtils.formatDuration(durationSec);
            propertiesOfAudioFile.put("SÜRE(s)", durationSec);
            propertiesOfAudioFile.put("SÜRE", formatted);

            Path songPath = Paths.get(fileName);
            Mp3Info songInfo = null;
            try {
                songInfo = Mp3Info.of(songPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int songLengthSeconds = songInfo.getSeconds();

            propertiesOfAudioFile.put("SÜRE(s) YENİ", songLengthSeconds);
            propertiesOfAudioFile.put("SÜRE YENİ", MP3DurationUtils.formatDuration(songLengthSeconds));
            //}

            AudioPropertiesDialog.showAudioPropertiesDialog((Frame) SwingUtilities.getWindowAncestor(this), propertiesOfAudioFile);
            fileInfoToggle.setSelected(false); // pencere kapandiginda file info toogle buton eski haline doner.

        } else {
            System.out.println("to see fileinfo, select an audio file..");
        }

    }

    /**
     * Used by the Play/Pause button
     */
    private void tooglePlay() throws BasicPlayerException {
        if (m3U8PlaylistManager.getTrackList().isEmpty()) {
            return;
        }
        // if (!player.isPaused()) {
        //   player.pause();
        //btnPlay.setIcon(playIcon);
        //   playToggle.setText("Play");
        //  } else {

        String fileName = m3U8PlaylistManager.getCurrentTrack().getUri();
        File file = new File(fileName);

        try {
            if (!PlayerEvent.fromEventId(eventCode).equals(PlayerEvent.PLAYING)) {
                player.open(file);
                player.play();
            } else {
                player.pause();
            }

        } catch (BasicPlayerException e) {
            throw new RuntimeException(e);
        }
            /* TODO
            currenTrackData = m3U8PlaylistManager.getTrackList().get(playListPanel.getSelectedPlaylistItemIndex());



            // turker
            System.out.println(playListPanel.getSelectedPlaylistItemIndex());
            player.addSong(currenTrackData.getUri());
            player.play();
            */

            /*
            if (currenTrackData.getTrackInfo() != null) {
                fileNameLabel.setText(currenTrackData.getTrackInfo().title);
            } else {
                fileNameLabel.setText(currenTrackData.getUri());
            }
            */

        // player.setIndexSong(playListPanel.getSelectedPlaylistItem());
        // }
    }

    private void triggerVolume() {
        setVolume(((float) volumeSlider.getValue()) / 100);
    }

    private void updateTimers() {
        /*
        if (!player.isPaused()) {
            long lms = player.getProgressMicroseconds();
            String timer0 = Utils.getMinutesRapp(player.getProgressMicroseconds());
            String timer1 = Utils.getMinutesRapp((long) (currentAudioDurationSec * 1000000) - player.getProgressMicroseconds());
            currentDurationLabel.setText(timer0);
            totalDurationLabel.setText(timer1);
            speedLabel.setText(String.valueOf(player.getAudioDurationSeconds()));

        }

         */
    }

    JDialog playListManagerDialog;

    /**
     * Bunu kullanmak zorundayiz cunku konumlandirmayi frame'a gore yapinca istedigim hizalamayi aliyorum.
     */
    JFrame mainFrame;

    // TODO modali false yapip dene
    private void showPlayListPanel() {
        // Eğer pencere halen yoksa yaratıyoruz
        if (playListManagerDialog == null) {

            mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Ana pencereye bir ComponentListener ekleyip hareketini izliyoruz.
            mainFrame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    // Eğer PlayList penceresi açık ve henüz kullanıcı tarafından taşınmadıysa,
                    // ana pencere hareket ettikçe aynı offset ile onu da taşıyoruz.
                    if (playListManagerDialog != null && playListManagerDialog.isVisible() && followMainWindow) {
                        movePlayListNextToMain();
                    }
                }
            });

            playListManagerDialog = new JDialog(
                    mainFrame,
                    "Playlist Manager",
                    false
            );
            playListManagerDialog.getContentPane().add(playListManagerPanel);
            playListManagerDialog.setSize(420, 400);
            //playListManagerDialog.setLocationRelativeTo(this);
            playListManagerDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);//.DISPOSE_ON_CLOSE);

            // ---- 1) Ekranda ilk açılış konumunu belirle ----
            // Daha önce kullanıcı bu pencereyi taşıyarak kapattıysa,
            // bıraktığı konumu hatırlıyoruz.
            if (lastPlayListPanelLocation != null) {
                playListManagerDialog.setLocation(lastPlayListPanelLocation);
                // Kullanıcı taşıyarak kapattığı için, ana pencereyi artık takip etmesin:
                followMainWindow = false; //false idi. True da yapsam false da yapsam anlamadim ama gerek yok.
            } else {
                // İlk defa açılıyorsa, ana pencereyi takip eden default konuma yerleştir:
                movePlayListNextToMain();
                followMainWindow = true; // true idi. ellemedim.
            }

            // ---- 2) Kullanıcı bu pencereyi hareket ettirdi mi diye dinleyelim ----
            playListManagerDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    // Eğer ilk ayarladığımız offsetten farklı bir noktaya taşınmışsa,
                    // demek ki kullanıcı penceresini elle sürüklemiş. O zaman followMainWindow = false yapalım.
                    if (followMainWindow) {
                        Point mainLoc = PlayerPanel.this.getLocationOnScreen();
                        int defaultX = mainLoc.x + PlayerPanel.this.getWidth() + 5;
                        int defaultY = mainLoc.y;
                        Point newLoc = playListManagerDialog.getLocationOnScreen();

                        if (newLoc.x != defaultX || newLoc.y != defaultY) {
                            followMainWindow = true; // false idi. TRUE yaptim ve artik hep ana pencereyi takip ediyor hemen onun yanina konumlanip.
                        }
                    }
                }
            });

            // ---- 3) Pencere kapatıldığında son konumunu hatırlayalım ----
            playListManagerDialog.addWindowListener(new WindowAdapter() {

                /*
                @Override
                public void windowClosing(WindowEvent e) {
                    // Pencere hala ekrandayken konumunu sakla
                    lastPlayListPanelLocation = playListManagerDialog.getLocation();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    // Bu referansı sıfırlayalım ki tekrar oluştururken yukarıdaki
                    // if(playListManagerDialog == null) bloğuna girebilelim.
                    playListToggle.setSelected(false);
                    playListManagerDialog = null;
                }
                */

                @Override
                public void windowDeactivated(WindowEvent e) {
                    // playlist Penceresi görünmez olduğunda konumunu sakla
                    lastPlayListPanelLocation = playListManagerDialog.getLocation();
                    // playlist penceresi hide edildiginde playerdaki playlist dugmesini eski haline getir.
                    if (!playListManagerDialog.isVisible()) {
                        playListToggle.setSelected(false);
                    }
                }


            });


        }

        playListManagerDialog.setVisible(true);
        playListManagerDialog.toFront();
    }

    private int marqueeIndex = 0;
    private boolean scrollDirectionRight = false; // true: sağa kayar, false: sola kayar
    private int marqueeSpeed = 3; // Kaç karakterde bir kayacak (örn. 3 olursa her güncellemede 3 karakter kayar)
    private int maxCharactersBeforeScroll = 40; // Kaç karakterden büyükse kaydırmaya başlasın

    private void updatePlayingText() {


        //    if (player.isPaused()) { TODO
        //      return;
        //    }
        /*
        if (songList == null || songList.isEmpty()) {
            return;
        }
        */

        if (m3U8PlaylistManager.getCurrentTrack() != null) { // uygulama açıldığında hata vermemesi için

            String currentSongName = m3U8PlaylistManager.getCurrentTrack().getUri();

            if (currentSongName.length() > maxCharactersBeforeScroll) {
                int visibleLength = maxCharactersBeforeScroll;

                if (scrollDirectionRight) {
                    marqueeIndex -= marqueeSpeed;
                    if (marqueeIndex < 0) {
                        marqueeIndex = currentSongName.length();
                    }
                } else {
                    marqueeIndex += marqueeSpeed;
                    if (marqueeIndex > currentSongName.length()) {
                        marqueeIndex = 0;
                    }
                }

                // Döngüsel kaydırma efekti
                String displayText = currentSongName.substring(marqueeIndex) + " " + currentSongName.substring(0, marqueeIndex);
                fileNameLabel.setText(displayText.substring(0, Math.min(displayText.length(), visibleLength)));
            } else {
                fileNameLabel.setText(currentSongName);
            }
        }

    }


    // Test amaçlı main metodu (PlayerPanel'i bir çerçevede göstermek için)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlayerPanel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            PlayerPanel panel = new PlayerPanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


    @Override
    public String getTabName() {
        return "plugin.sound.mp3.player.jlayer.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.SOUND;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }

    @Override
    public void mute() {
        setVolume(0.0f);
        volume = 0;
    }

    @Override
    public void unmute() {
        volume = ((float) volumeSlider.getValue()) / 100;
        setVolume(volume);
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    // BASLA interface aracigiliyla playlistteki click eventlerini almak icin.
    @Override
    public void onItemClicked(int index, TrackData itemValue) {

    }

    @Override
    public void onItemDoubleClicked(int index, TrackData itemValue) {
        fileNameLabel.setText(itemValue.getUri());
        TrackData trackData = m3U8PlaylistManager.getTrackList().get(index);
        if (trackData != null) {
            currenTrackData = trackData;
            //System.out.println(currenTrackData.getUri());
            fileNameLabel.setText(currenTrackData.getUri());
            try {
                boolean continueToPlay = PlayerEvent.fromEventId(eventCode).equals(PlayerEvent.PLAYING);
                player.stop();
                player.open(new File(currenTrackData.getUri()));
                seekbar.updateSeekBar(getProgressMicroseconds(), audioDurationInSeconds);
                //if (continueToPlay) {
                player.play();
                //}

            } catch (BasicPlayerException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onItemRightClicked(int index, TrackData itemValue) {

    }
    // BITTI inteface aracigiliyla playlistteki click eventlerini almak icin.

    /*
    @Override
    public void opened(Object stream, Map properties) {

    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {

        csms = microseconds;
        cpcmdata = pcmdata;
    }

    @Override
    public void stateUpdated(BasicPlayerEvent basicPlayerEvent) {

    }

    @Override
    public void setController(BasicController basicController) {

    }
*/

    /*
    public void play(String filename) {
        // Instantiate BasicPlayer.
        //BasicPlayer player = new BasicPlayer();
        // BasicPlayer is a BasicController.
        //BasicController control = (BasicController) player;
        // Register BasicPlayerTest to BasicPlayerListener events.
        // It means that this object will be notified on BasicPlayer
        // events such as : opened(...), progress(...), stateUpdated(...)
        //player.addBasicPlayerListener(this);

        try {
            // Open file, or URL or Stream (shoutcast, icecast) to play.
            control.open(new File(filename));

            // control.open(new URL("http://yourshoutcastserver.com:8000"));

            // Start playback in a thread.
            control.play();

            // If you want to pause/resume/pause the played file then
            // write a Swing player and just call control.pause(),
            // control.resume() or control.stop().
            // Use control.seek(bytesToSkip) to seek file
            // (i.e. fast forward and rewind). seek feature will
            // work only if underlying JavaSound SPI implements
            // skip(...). True for MP3SPI and SUN SPI's
            // (WAVE, AU, AIFF).

            // Set Volume (0 to 1.0).
            control.setGain(0.85);
            // Set Pan (-1.0 to 1.0).
            control.setPan(0.0);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }
    */

    public void stop() {
        try {
            control.stop();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            control.pause();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            control.resume();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        try {
            control.setGain(volume);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ana pencereyi referans alarak, PlayList penceresini sağda +5 px offset
     * ve aynı üst koordinattan konumlandırır.
     */
    private void movePlayListNextToMain() {
        Point mainLoc = mainFrame.getLocationOnScreen();
        int x = mainLoc.x + this.getWidth() + 5; // +5 px sağında
        int y = mainLoc.y;                      // aynı üst koordinat
        playListManagerDialog.setLocation(x, y);
    }


    // BASLA seekbar ile ilgili

    //Frames
    //private WaveformParallelFrame wff = null;
    //private FFTParallelFrame fdf = null;
    private boolean isSeeking = false;

    //Current Audio Properties
    private float audioDurationInSeconds = 0;
    private int audioFrameSize = 0;
    private float audioFrameRate = 0;
    //Stream info/status
    private byte[] cpcmdata;
    private long csms = 0; //Current Song microseconds
    private int lastSeekMs = 0; //Every time we seek, basic player returns microseconds are resetted

    //we need a var to mantain the position we seeked to
    public byte[] getPcmData() {
        return cpcmdata;
    }

    public long getProgressMicroseconds() {
        return csms + lastSeekMs;
    }

    public float getAudioDurationSeconds() {
        return audioDurationInSeconds;
    }

    public float getAudioFrameRate() {
        return audioFrameRate;
    }

    public float getAudioFrameSize() {
        return audioFrameSize;
    }

    /**
     * Remembers what's the last position relative to the playing song when
     * seeking
     */
    public void setLastSeekPositionInMs(int seekMs) {
        lastSeekMs = seekMs;
    }

    // BITTI seekbar ile ilgili


}
