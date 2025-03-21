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
package com.turkerozturk.jpanels.pomodoro;

import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.jpanels.pomodoro.subpanels.EndingSoundPanel;
import com.turkerozturk.jpanels.pomodoro.subpanels.TickSoundWavPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// PomodoroService sınıfını daha önce verdiğimiz kodla oluşturduğunuzu varsayıyoruz.
public class PomodoroAppPanel extends JPanel{

    private static final Logger logger = LoggerFactory.getLogger(PomodoroAppPanel.class);


    private TickSoundWavPanel tickSoundPanel;
    private EndingSoundPanel endingSoundPanel;
    private JLabel remainingLabel, sessionLabel;
    private JToggleButton startStopButton;
    private JButton resetButton;
    private JButton nextButton;
    private JToggleButton autoPlayToggle;

    private JSpinner workSpinner;
    private JSpinner shortBreakSpinner;
    private JSpinner longBreakSpinner;

    private JSpinner sessionCountSpinner;


    // PomodoroService nesnesi
    private PomodoroService service;

    // Swing Timer: display paneli periyodik güncellemek için
    private Timer displayUpdater;

    // Durum takibi için: Timer çalışıyorsa "stop" butonuyla durdurulmalı
    private boolean isRunning = false;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();

    private void prepareNExt() {
        service.next();
        // Butonlardan gelen reset sonrasında spinner'ları da güncelleyelim
        updateSpinnersFromService();
        //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
        updateDisplay();
        // Timer durduysa, isRunning false olarak kalır.
        if(autoPlayToggle.isSelected()) {
            service.start();
            isRunning = true;
            startStopButton.setText("Stop");
            startStopButton.setSelected(true);
        } else {
            isRunning = false;
            startStopButton.setText("Start");
            startStopButton.setSelected(false);
        }
    }

    public PomodoroAppPanel() {
        service = new PomodoroService();

        initialize();
        startDisplayUpdater();

        // start tick sound related code
        service.addTimerTickListener(new TimerTickListener() {
            @Override
            public void onTick(int remainingSeconds) {
                // İsterseniz kalan süre güncellenirken tick sound efektini çalıştırın.
                if(service.getActiveTimerType() == PomodoroService.PomodoroTimerType.WORK_TIME) {
                    tickSoundPanel.tick();
                } else if(service.getActiveTimerType() == PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                    // SILENT while resting during long break. Dont forget here is ticking each second.
                } else {
                    // SILENT while resting during long break. Dont forget here is ticking each second.
                }
            }
        });
        // end tick sound related code

        // start ending sound related code
        service.addTimerFinishedListener(new TimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                endingSoundPanel.playFrequencyBeepIfSelected();
                if(!autoPlayToggle.isSelected()) {

                    prepareNExt();
                } else {
                    // else autoplay is  switching to next already. This if block prevents from double forward.
                    // autoplay config dosyasinda 1 iken ustteki if den dolayi pas geciyordu, acilista
                    // autoplay buttonun isSelected durumuna gore ser selected yapinca duzeldi problem..
                }
            }
        });
        // end ending sound related code


    }

    private void initialize() {



        // JSplitPane: Üst (display) ve alt (tabbed) paneller
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(100);
        //splitPane.setResizeWeight(0.2);
        add(splitPane);

        // Üst Panel: Display Panel
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());

        // Kalan süreyi gösteren etiket
        remainingLabel = new JLabel(formatTime(service.getRemainingSeconds()), SwingConstants.CENTER);
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        remainingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Çalışma seans bilgisini gösterecek etiket
        sessionLabel = new JLabel(getSessionInfo(), SwingConstants.CENTER);
        sessionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sessionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Üst kısmı iki label olacak şekilde alt alta ekleyelim:
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.add(remainingLabel);
        timePanel.add(sessionLabel);


        // timePanel'in displayPanel içinde ortalanması için
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(timePanel);

        displayPanel.add(wrapperPanel, BorderLayout.CENTER);


        // Buton paneli
        JPanel buttonPanel = new JPanel();
        startStopButton = new JToggleButton("Start");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next");
        autoPlayToggle = new JToggleButton();
        autoPlayToggle.setSelected(service.isAutoPlayEnabled());
        if(service.isAutoPlayEnabled()) {
            autoPlayToggle.setText(bundle.getString("autoplay.on"));
        } else {
            autoPlayToggle.setText(bundle.getString("autoplay.off"));
        }

        buttonPanel.add(startStopButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(autoPlayToggle);
        displayPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Butonların listener'ları
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    service.start();
                    isRunning = true;
                    startStopButton.setText("Stop");
                } else {
                    service.stop();
                    isRunning = false;
                    startStopButton.setText("Start");
                }
            }
        });



        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset: aktif zamanlayıcının spinner'da ayarlanan değeri kullanılır.
                // Eğer aktif tabda değişiklik yapıldıysa, reset metodu spinnerdaki değeri alarak yeniden ayarlar.
                if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.WORK_TIME) {
                    service.setWorkDurationMinutes((Integer) workSpinner.getValue());
                } else if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                    service.setShortBreakDurationMinutes((Integer) shortBreakSpinner.getValue());
                } else if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.LONG_BREAK) {
                    service.setLongBreakDurationMinutes((Integer) longBreakSpinner.getValue());
                }
                service.reset();
                //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                updateDisplay();
                isRunning = false;
                startStopButton.setText("Start");
                startStopButton.setSelected(false);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prepareNExt();
            }
        });

        autoPlayToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = autoPlayToggle.isSelected();
                service.enableAutoPlay(selected);
                autoPlayToggle.setText(selected ?
                        bundle.getString("autoplay.on")
                        : bundle.getString("autoplay.off"));
            }
        });

        splitPane.setTopComponent(displayPanel);

        // Alt Panel: Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel allTimersPanel = new JPanel();
        allTimersPanel.setLayout(new GridLayout(5, 1, 1, 1));


        // Work tab
        //JPanel workPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        allTimersPanel.add(new JLabel("Work Duration (min): "));
        workSpinner = new JSpinner(new SpinnerNumberModel(service.getWorkDurationMinutes(), 1, 1440, 1));
        allTimersPanel.add(workSpinner);
        //tabbedPane.addTab("Work", workPanel);

        // Short Break tab
        //JPanel shortBreakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        allTimersPanel.add(new JLabel("Short Break (min): "));
        shortBreakSpinner = new JSpinner(new SpinnerNumberModel(service.getShortBreakDurationMinutes(), 1, 1440, 1));
        allTimersPanel.add(shortBreakSpinner);
        //tabbedPane.addTab("Short Break", shortBreakPanel);

        // Long Break tab
        //JPanel longBreakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        allTimersPanel.add(new JLabel("Long Break (min): "));
        longBreakSpinner = new JSpinner(new SpinnerNumberModel(service.getLongBreakDurationMinutes(), 1, 1440, 1));
        allTimersPanel.add(longBreakSpinner);
        //tabbedPane.addTab("Long Break", longBreakPanel);

        allTimersPanel.add(new JLabel("Session Count: "));
        sessionCountSpinner = new JSpinner(new SpinnerNumberModel(service.getTotalWorkSessions(), 1, 100, 1));
        // https://stackoverflow.com/questions/2902101/how-to-set-jspinner-as-non-editable
        ((JSpinner.DefaultEditor) sessionCountSpinner.getEditor()).getTextField().setEditable(false);
        allTimersPanel.add(sessionCountSpinner);

        tabbedPane.addTab("All Timers", allTimersPanel);

        // Spinner listener'ları:
        ChangeListener spinnerListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Aktif tab'a göre ilgili süreyi güncelle.
                //int selectedIndex = tabbedPane.getSelectedIndex();
                Object source = e.getSource();

                if (source == workSpinner) {
                    int newVal = (Integer) workSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.WORK_TIME) {
                        // Aktifse updateActiveTimerDuration kullanarak kalan süreyi de güncelleyelim
                        service.updateActiveTimerDuration(newVal);
                        //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setWorkDurationMinutes(newVal);
                    }
                } else if (source == shortBreakSpinner) {
                    int newVal = (Integer) shortBreakSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                        service.updateActiveTimerDuration(newVal);
                        //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setShortBreakDurationMinutes(newVal);
                    }
                } else if (source == longBreakSpinner) {
                    int newVal = (Integer) longBreakSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.LONG_BREAK) {
                        service.updateActiveTimerDuration(newVal);
                        //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setLongBreakDurationMinutes(newVal);
                    }
                } else if(source == sessionCountSpinner) {
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.WORK_TIME) {

                        int currentVal = service.getCurrentWorkSession();
                        int newVal = (Integer) sessionCountSpinner.getValue();
                        if (newVal >= currentVal) {
                            service.setTotalWorkSessions(newVal);
                            //sessionCountSpinner.setValue(newVal);
                        } else {
                            sessionCountSpinner.setValue(currentVal);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "You cannot change\n the pomodoro count\n during break times!");

                    }
                }
                updateDisplay();
            }
        };

        workSpinner.addChangeListener(spinnerListener);
        shortBreakSpinner.addChangeListener(spinnerListener);
        longBreakSpinner.addChangeListener(spinnerListener);
        sessionCountSpinner.addChangeListener(spinnerListener);


        // Tab değiştiğinde, aktif timer türüyle ilgili spinner'ı göstermek amacıyla
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                // Eğer aktif timer tipi farklı ise, değiştirilmiş spinner değerinin display'e yansıması için reset çağırabilirsiniz.
                if (selectedIndex == 0 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.WORK_TIME) {
                    service.setWorkDurationMinutes((Integer) workSpinner.getValue());
                    //service.reset();
                    //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                } else if (selectedIndex == 1 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                    service.setShortBreakDurationMinutes((Integer) shortBreakSpinner.getValue());
                    //service.reset();
                    //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                } else if (selectedIndex == 2 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.LONG_BREAK) {
                    service.setLongBreakDurationMinutes((Integer) longBreakSpinner.getValue());
                    //service.reset();
                    //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                }
                updateDisplay();
            }
        });

        tickSoundPanel = new TickSoundWavPanel();
        tabbedPane.addTab(translate("tab.panel.tick.sound.title"), tickSoundPanel);
        tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(tickSoundPanel));

        endingSoundPanel = new EndingSoundPanel();
        tabbedPane.addTab(translate("tab.panel.ending.sound.title"), endingSoundPanel);

        splitPane.setBottomComponent(tabbedPane);
        //setVisible(true);
    }

    // Swing Timer ile display panelde kalan süreyi periyodik güncelle
    private void startDisplayUpdater() {
        displayUpdater = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                updateDisplay();
            }
        });
        displayUpdater.start();
    }

    // Kalan saniyeyi "MM:SS" formatında döner.
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Spinner'ların değerlerini servisten güncelle
    private void updateSpinnersFromService() {
        workSpinner.setValue(service.getWorkDurationMinutes());
        shortBreakSpinner.setValue(service.getShortBreakDurationMinutes());
        longBreakSpinner.setValue(service.getLongBreakDurationMinutes());
    }

    /*
    public static void main(String[] args) {
        // Swing UI Event Dispatch Thread üzerinde çalıştır.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PomodoroApp();
            }
        });
    }
    */



    public String translate(String key) {
        return bundle.getString(key);
    }

    // start session info text

    // Display panelde kalan süre ve session label'ı güncelleyen metot
    private void updateDisplay() {
        remainingLabel.setText(formatTime(service.getRemainingSeconds()));
        sessionLabel.setText(getSessionInfo());
       // logger.info(String.format("remaining: %s, session %s",formatTime(service.getRemainingSeconds()), getSessionInfo()));
    }

    // Aktif timer türüne göre seans bilgisini döner
    private String getSessionInfo() {
        PomodoroService.PomodoroTimerType type = service.getActiveTimerType();
        if (type == PomodoroService.PomodoroTimerType.WORK_TIME) {
            return "Work Time " + service.getCurrentWorkSessionAsString() + " of " + service.getTotalWorkSessions();
        } else if (type == PomodoroService.PomodoroTimerType.SHORT_BREAK) {
            return "Short Break " + service.getCurrentWorkSessionAsString() + " of " + service.getTotalWorkSessions();
        } else { // LONG_BREAK
            return "Long Break";
        }
    }


    // end session info text


    public TickSoundWavPanel getTickSoundPanel() {
        return tickSoundPanel;
    }

    public EndingSoundPanel getEndingSoundPanel() {
        return endingSoundPanel;
    }
}

