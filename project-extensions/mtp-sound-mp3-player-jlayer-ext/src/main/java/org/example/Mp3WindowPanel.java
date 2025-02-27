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
/*
 * Portions of this file are derived from the Musicplayer project
 * (https://github.com/Velliz/Musicplayer), specifically from the commit:
 * https://github.com/Velliz/Musicplayer/commit/41c4c5ee21a21a845865c34c2b847c75d3349604
 *
 * The original code is licensed under the Apache License Version 2.0,
 * January 2004 (http://www.apache.org/licenses/).
 *
 * As per the requirements of the Apache License, the original copyright
 * notice, license text, and disclaimer must be retained:
 *
 *  --- Begin Apache License Notice ---
 *  Copyright (c) 2017 Velliz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  --- End Apache License Notice ---
 */
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import org.example.thirdparty.AudioPlayer;
import org.example.thirdparty.SeekBar;

import org.example.thirdparty.utils.BackgroundExecutor;
import org.example.thirdparty.utils.StatusFrame;
import org.example.thirdparty.utils.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dwi Fadhil Didit
 */
public class Mp3WindowPanel extends JPanel implements PanelPlugin {

    private double volume = 0.1;

    //Other
    private DefaultListModel<String> songList = new DefaultListModel<>();
    private ScheduledExecutorService timersExec = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService titleExec = Executors.newSingleThreadScheduledExecutor();
    private float currentAudioDurationSec = 0;

    //AudioPlayer
    private AudioPlayer player = AudioPlayer.getInstance();

    private JFileChooser fc = new JFileChooser();

    //Frames
    //private WaveformParallelFrame wff = null;
    //private FFTParallelFrame fdf = null;
    public static StatusFrame stf = new StatusFrame();

    //Icons
    private ImageIcon frameIcon = new ImageIcon(getClass().getResource("/res/frameicon.png"));
    private ImageIcon playIcon = new ImageIcon(getClass().getResource("/res/playicon.png"));
    private ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/res/pauseicon.png"));

    private SeekBar seekbar = new SeekBar();

    public Mp3WindowPanel() {
        initComponents();
      //  setIconImage(frameIcon.getImage());
      //  setTitle("Music Player - Java - 1.0");
      //  setLocationRelativeTo(null);
      //  setDefaultCloseOperation(EXIT_ON_CLOSE);
      //  setResizable(false);
        jPanel1.add(seekbar);
        uiBehaviour();

    }

    private void triggerVolume() {
        try {
            player.setVolume(((double) volslide.getValue()) / 100);
        } catch (BasicPlayerException e1) {
            e1.printStackTrace();
        }
    }

    private void uiBehaviour() {
        //File chooser
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "only supported audio files (mp3, wav)";
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                if (f.getName().endsWith(".mp3")) {
                    return true;
                }
                if (f.getName().endsWith(".wav")) {
                    return true;
                }
                return false;
            }
        });

        menuAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(btnAdd);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    for (File f : files) {
                        player.addSong(f.getAbsolutePath());
                        songList.addElement(f.getName());
                        log("Added file " + f.getName() + " to playlist");
                    }
                } else {
                    log("No file selected");
                }
            }
        });

        //Song List
        jSongList.setModel(songList);
        jSongList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jSongList.setLayoutOrientation(JList.VERTICAL);
        //Event that triggers at double click
        jSongList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    log("Double click detected, moving to selected item.");
                    int index = list.locationToIndex(evt.getPoint());
                    player.setIndexSong(index);
                    try {
                        player.play();
                    } catch (BasicPlayerException ev) {
                        ev.printStackTrace();
                    }
                }
            }
        });

        //Btn Delete
        menuDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Executed Outside UI Thread
                BackgroundExecutor.get().execute(new Runnable() {

                    @Override
                    public void run() {
                        int[] indexes = jSongList.getSelectedIndices();
                        int removed = 0;
                        for (int i : indexes) {
                            log("Removed Song (" + (i - removed) + ")" + songList.get(i - removed));
                            player.removeSong(i - removed);
                            songList.remove(i - removed);
                            removed++;
                        }
                    }
                });
            }
        });
        //Play Btn
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    tooglePlay();
                } catch (BasicPlayerException e1) {
                    e1.printStackTrace();
                }
            }
        });
        //Next and Previous btns
        btnNext.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    player.nextSong();
                } catch (BasicPlayerException e) {
                    log("Error calling the next song");
                    e.printStackTrace();
                }
            }
        });

        btnPrev.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    player.prvSong();
                    //seekbar.resetLastSeek();
                } catch (BasicPlayerException e) {
                    log("Error calling the previous song");
                    e.printStackTrace();
                }
            }
        });

        //Player related behaviour
        player.addBasicPlayerListener(new BasicPlayerListener() {

            @Override
            public void stateUpdated(BasicPlayerEvent event) {
                if (event.getCode() == BasicPlayerEvent.EOM) {
                    //seekbar.resetLastSeek();
                    try {
                        if (!repeat.isSelected()) {
                            player.nextSong();
                        } else {
                            player.play();
                        }
                    } catch (BasicPlayerException e) {
                        e.printStackTrace();
                    }
                    log("EOM event catched, calling next song.");
                }
                if (event.getCode() == BasicPlayerEvent.PAUSED) {
                    btnPlay.setIcon(playIcon);
                }
                if (event.getCode() == BasicPlayerEvent.RESUMED) {
                    btnPlay.setIcon(pauseIcon);
                }
            }

            @Override
            public void setController(BasicController arg0) {
            }

            @Override
            public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
                //we don't want to use microseconds directly because it gets resetted on seeking
                seekbar.updateSeekBar(player.getProgressMicroseconds(), currentAudioDurationSec);
                /*if (wff != null) {
                    wff.updateWave(pcmdata);
                }
                if (fdf != null) {
                    fdf.updateWave(pcmdata);
                }*/
            }

            @Override
            public void opened(Object arg0, Map arg1) {
                btnPlay.setIcon(pauseIcon);
                jSongList.setSelectedIndex(player.getIndexSong());
                //jTextArea1.setText("Now Playing: " + songList.get(player.getIndexSong()));
                currentAudioDurationSec = player.getAudioDurationSeconds();
            }
        });

        timersExec.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                updateTimers();
            }
        }, 0, 1, TimeUnit.SECONDS);

        titleExec.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                updatePlayingText();
            }
        }, 0, 1, TimeUnit.SECONDS);

        volslide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                triggerVolume();
            }
        });

    }

    /**
     * Used by the Play/Pause button
     */
    private void tooglePlay() throws BasicPlayerException {
        if (songList.size() == 0) {
            return;
        }
        if (!player.isPaused()) {
            player.pause();
            btnPlay.setIcon(playIcon);
        } else {
            player.play();
        }
    }

    private void updateTimers() {
        if (!player.isPaused()) {
            long lms = player.getProgressMicroseconds();
            String timer0 = Utils.getMinutesRapp(player.getProgressMicroseconds());
            String timer1 = Utils.getMinutesRapp((long) (currentAudioDurationSec * 1000000) - player.getProgressMicroseconds());
            lblst.setText(timer0);
            lblet.setText(timer1);
        }
    }

    int dispIndex = 0;
    boolean goback = false;
    final static int MAXLblPChar = 36;

    private void updatePlayingText() {
        if (player.isPaused()) {
            return;
        }
        if (songList == null || (songList.size() == 0)) {
            return;
        }
        String currentSong = songList.get(player.getIndexSong());
        if (currentSong.length() > MAXLblPChar) {
            if ((MAXLblPChar + dispIndex) >= currentSong.length()) {
                goback = true;
            }
            if (dispIndex == 0) {
                goback = false;
            }
            String cutStr = currentSong.substring(dispIndex, MAXLblPChar + dispIndex);
            //jTextArea1.setText("Now Playing: " + cutStr);
            if (!goback) {
                dispIndex++;
            } else {
                dispIndex--;
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel(new SyntheticaBlueIceLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Mp3WindowPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Mp3WindowPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Mp3WindowPanel mv = new Mp3WindowPanel();
                mv.setVisible(true);
            }
        });
    }

    private void log(String line) {
        //jTextArea1.append("UI-Main] " + line + "\n");
        stf.addText("UI-Main] " + line);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu6 = new JMenu();
        //buttonGroup1 = new ButtonGroup();
        backgrounD = new JPanel();
        // jLabel1 = new JLabel();
        // jRadioButton2 = new JRadioButton();
        // jRadioButton3 = new JRadioButton();
        // jRadioButton1 = new JRadioButton();
        // jComboBox1 = new JComboBox<String>();
        jScrollPane2 = new JScrollPane();
        jSongList = new JList();
        jPanel1 = new JPanel();
        btnPrev = new JButton();
        btnNext = new JButton();
        btnPlay = new JButton();
        lblst = new JLabel();
        lblet = new JLabel();
        repeat = new JCheckBox();
        jButton2 = new JButton();
        volslide = new JSlider();
        jLabel2 = new JLabel();
        jRadioButton4 = new JRadioButton();
        jMenuBar1 = new JMenuBar();
        btnAdd = new JMenu();
        menuAdd = new JMenuItem();
        menuDelete = new JMenuItem();
        jMenu2 = new JMenu();
        jMenuItem3 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenu3 = new JMenu();

        jMenu6.setText("jMenu6");

        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //jLabel1.setFont(new Font("Papyrus", 0, 24)); // NOI18N
        //jLabel1.setText("DFD MP3 PLAYER");
        /*
        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Kuning");
        jRadioButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Hijau");
        jRadioButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Merah");
        jRadioButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        */

        /*
        jComboBox1.setModel(new DefaultComboBoxModel(new String[] { "Status", "Ekualizer Frekuensi", "Wave Frekuensi" }));
        jComboBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        */
        jSongList.setModel(new AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane2.setViewportView(jSongList);

        jPanel1.setBackground(new Color(153, 255, 153));

        btnPrev.setText("<");

        btnNext.setText(">");

        btnPlay.setText("PLAY");

        lblst.setText("start");

        lblet.setText("end");

        repeat.setText("Repeat");

        jButton2.setText("MUTE");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Daftar Putar");

        /*
        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Abu-abu");
        jRadioButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });
        */


        backgrounD.add(jScrollPane2);
        backgrounD.add(jLabel2);
        backgrounD.add(btnPrev);
        backgrounD.add(lblst);
        backgrounD.add(lblet);
        backgrounD.add(btnNext);
        backgrounD.add(jPanel1);
        backgrounD.add(btnPlay);
        backgrounD.add(repeat);
        backgrounD.add(jButton2);
        backgrounD.add(volslide);
        //backgrounD.add(jLabel1);
        backgrounD.add(volslide);
        backgrounD.add(btnNext);
        backgrounD.add(btnPrev);
        backgrounD.add(lblet);
        backgrounD.add(lblst);
        backgrounD.add(jScrollPane2);
        backgrounD.add(lblet);
        backgrounD.add(lblet);
        backgrounD.add(lblet);

        //GroupLayout backgrounDLayout = new GroupLayout(backgrounD);
        ///backgrounD.setLayout(backgrounDLayout);


        btnAdd.setText("File");

        menuAdd.setText("Add Audio File");
        btnAdd.add(menuAdd);

        menuDelete.setText("Delete Audio File");
        btnAdd.add(menuDelete);

        jMenuBar1.add(btnAdd);

        jMenu2.setText("About");

        jMenuItem3.setText("Tentang Aplikasi");
        jMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem2.setText("Tentang Pengembang");
        jMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Exit");
        jMenu3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jMenu3MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        add(jMenuBar1);




        add(backgrounD);

        //pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu3MouseClicked(MouseEvent evt) {//GEN-FIRST:event_jMenu3MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jMenu3MouseClicked

    private void jMenuItem3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JOptionPane.showMessageDialog(this, "Aplikasi ini adalah aplikasi untuk memutar lagu \n"
                + "Dibuat untuk tugas PBO Lanjut - 25 Februari 2016 \n"
                + "Pilih File - Tambah Lagu - Ke lokasi File lagu Anda, Lalu OK", "Tentang Aplikasi", 1);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JOptionPane.showMessageDialog(this,
                "------------Pengembang Aplikasi-----------\n"
                        + "1575004      Nurcholid Achmad     \n"
                        + "1575010 Fadhil Hafizh Ardiansyah  \n"
                        + "1575002    Dwi Paulina Brahmana   \n"
                        + "------------DFD MP3 Player--------------\n"
                        + "----------Didit - Fadhil - Dwi----------",
                "Tentang Pengembang", 1);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            if (volume != 0) {
                player.setVolume(0.0);
                volume = 0;
            } else {
                volume = ((double) volslide.getValue()) / 100;
                player.setVolume(volume);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Mp3WindowPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /*
    private void jComboBox1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        switch (jComboBox1.getSelectedIndex()) {
            case 0:
                stf.setVisible(true);
                break;
            case 1:
              //  fdf = new FFTParallelFrame();
              //  fdf.setVisible(true);
                break;
            case 2:
              //  wff = new WaveformParallelFrame();
              //  wff.setVisible(true);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed
    */

    /*
    private void jRadioButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        backgrounD.setBackground(Color.RED);
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        backgrounD.setBackground(Color.GREEN);        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        backgrounD.setBackground(Color.YELLOW);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        backgrounD.setBackground(Color.LIGHT_GRAY);
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton4ActionPerformed
    */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel backgrounD;
    private JMenu btnAdd;
    private JButton btnNext;
    private JButton btnPlay;
    private JButton btnPrev;
    //private ButtonGroup buttonGroup1;
    private JButton jButton2;
    //private JComboBox<String> jComboBox1;
    //private JLabel jLabel1;
    private JLabel jLabel2;
    private JMenu jMenu2;
    private JMenu jMenu3;
    private JMenu jMenu6;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItem3;
    private JPanel jPanel1;
    private JRadioButton jRadioButton1;
    private JRadioButton jRadioButton2;
    private JRadioButton jRadioButton3;
    private JRadioButton jRadioButton4;
    private JScrollPane jScrollPane2;
    private JList jSongList;
    private JLabel lblet;
    private JLabel lblst;
    private JMenuItem menuAdd;
    private JMenuItem menuDelete;
    private JCheckBox repeat;
    private JSlider volslide;

    @Override
    public String getTabName() {
        return "plugin.sound.mp3.player.jlayer.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
    // End of variables declaration//GEN-END:variables
}
