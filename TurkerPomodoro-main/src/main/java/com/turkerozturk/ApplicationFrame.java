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

//import com.turkerozturk.astronomy.SunAndMoonPanel;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.PluginLoader;
import com.turkerozturk.initial.PluginsLoader;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;
//import com.turkerozturk.jpanels.about.AboutPanel;
//import com.turkerozturk.jpanels.analogclock.AnalogClockPanel;
//import com.turkerozturk.jpanels.binaural.BinauralPanel;
//import com.turkerozturk.jpanels.calculator.CalculatorPanel;
//import com.turkerozturk.jpanels.configuration.ConfigurationEditorPanel;
//import com.turkerozturk.jpanels.countdowntimer.CountdownTimerPanel;
//import com.turkerozturk.jpanels.datetime.DateTimePanel;
//import com.turkerozturk.jpanels.metronome.MetronomePanel;
//import com.turkerozturk.jpanels.mididevice.MidiDeviceTestPanel;
//import com.turkerozturk.jpanels.mp3.Mp3PlayerFx;
//import com.turkerozturk.jpanels.mp3.Mp3PlayerPanel;
//import com.turkerozturk.jpanels.noisegenerator.NoisePanel;
//import com.turkerozturk.jpanels.notes.NotesPanel;
//import com.turkerozturk.jpanels.paint.CanvasPanel;
//import com.turkerozturk.jpanels.piano.PianoPanel;
//import com.turkerozturk.jpanels.speakertest.AudioOutputPanel;
//import com.turkerozturk.jpanels.systeminfo.SystemInfoPanel;
//import com.turkerozturk.jpanels.taptempo.TapTempoTool;
import com.turkerozturk.jpanels.pomodoro.PomodoroAppPanel;
//import com.turkerozturk.jpanels.textquotes.RandomTextDisplayPanel;
import com.turkerozturk.jpanels.theme.ThemeSelectorPanel;
//import com.turkerozturk.plugin.PanelPlugin;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ApplicationFrame extends JFrame {
/*
        private final Mp3PlayerFx playerPanel;
        private final Mp3PlayerFx playerPanel2;
        private final Mp3PlayerFx playerPanel3;
*/
    private JButton globalMuteButton;

    private JButton muteButtonAtTab;

    private int frameWidth, frameHeight;


    JToggleButton toggleAlwaysOnTopButton, toggleHistoryLoggingButton;
    private boolean isAlwaysOnTop, isHistoryLoggingEnabled;

    Dimension defaultFrameDimension;


    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    private void loadVariablesFromConfig() {
        frameWidth = Integer.parseInt(props.getProperty("frame.width", "700"));
        frameHeight = Integer.parseInt(props.getProperty("frame.height", "350"));
    }

    private void initializeApplicationFrame() {
        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(frameWidth, frameHeight);
        defaultFrameDimension = getSize();
        setLocationRelativeTo(null);
    }

    public ApplicationFrame() {

        loadVariablesFromConfig();

        initializeApplicationFrame();



        toggleAlwaysOnTopButton = new JToggleButton(translate("frame.always.on.top"));



        isAlwaysOnTop = Integer.parseInt(props.getProperty("gui.is.always.on.top")) == 1;
        toggleAlwaysOnTopButton.setSelected(isAlwaysOnTop);
        if (toggleAlwaysOnTopButton.isSelected()) {
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.on"));
            setAlwaysOnTop(isAlwaysOnTop);
        } else {
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.off"));
        }


        JTabbedPane tabbedPanel = new JTabbedPane();

        PomodoroAppPanel pomodoroApp = new PomodoroAppPanel();
        tabbedPanel.addTab("Pomodoro", pomodoroApp);




        //JTabbedPane jTabbedPaneForNoises = new JTabbedPane();
        /*
        BinauralPanel binauralPanel = new BinauralPanel();
        jTabbedPaneForNoises.addTab(translate("tab.panel.binaural.beats.title"), binauralPanel);
        NoisePanel noisePanel = new NoisePanel();
        jTabbedPaneForNoises.addTab("Noise Generator", noisePanel.getPlayerPanel());
        */
        /*
        MetronomePanel metronomePanel = new MetronomePanel();
        jTabbedPaneForNoises.addTab("Metronome", metronomePanel.getPlayerPanel());
        */

        //tabbedPanel.addTab("Noise Generators", jTabbedPaneForNoises);

/*
        JTabbedPane jTabbedPaneForMp3 = new JTabbedPane();
         playerPanel = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player", playerPanel.getPlayerPanel());
         playerPanel2 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.2.file.location", "playlist2.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player2", playerPanel2.getPlayerPanel());
         playerPanel3 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.3.file.location", "playlist3.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        tabbedPanel.addTab("MP3 Players", jTabbedPaneForMp3);

        Mp3PlayerPanel mp3PlayerPanel = new Mp3PlayerPanel(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));

        tabbedPanel.addTab("New MP3", mp3PlayerPanel);
*/




        /*
        AnalogClockPanel analogClock = new AnalogClockPanel();
        jTabbedPaneForOtherTools.addTab("Clock", analogClock);
        */

        /*
        JTabbedPane textPanels = new JTabbedPane();
        RandomTextDisplayPanel randomTextDisplayPanel1 = new RandomTextDisplayPanel(1);
        RandomTextDisplayPanel randomTextDisplayPanel2 = new RandomTextDisplayPanel(2);
        RandomTextDisplayPanel randomTextDisplayPanel3 = new RandomTextDisplayPanel(3);
        textPanels.addTab("Quotes", randomTextDisplayPanel1);
        textPanels.addTab("To Do", randomTextDisplayPanel2);
        textPanels.addTab("Memorize", randomTextDisplayPanel3);
        jTabbedPaneForOtherTools.addTab("Texts", textPanels);
        */


        // BASLA pluginlerin yuklenmesi
        ServiceLoader<PanelPlugin> loader = PluginsLoader.loadPanelPlugins();
        if(loader!=null) {

            JTabbedPane jTabbedPaneForOtherTools = new JTabbedPane();

            // 4) Her bulunan plugin nesnesini GUI'ye ekle
            for (PanelPlugin plugin : loader) {
                String tabName;

                if (bundle.containsKey(plugin.getTabName())) {
                    tabName = bundle.getString(plugin.getTabName());
                } else {
                    tabName = plugin.getTabName(); // Varsayılan değer olarak plugin ismini kullan
                }

                // for debug System.out.println(tabName);
                jTabbedPaneForOtherTools.addTab(tabName, plugin.getPanel());
            }

            tabbedPanel.addTab("Other Tools", jTabbedPaneForOtherTools);


        }
        // BITTI pluginlerin yuklenmesi




        /*
        NotesPanel notesPanel = new NotesPanel();
        jTabbedPaneForOtherTools.addTab("Notes", notesPanel);
        CanvasPanel canvasPanel = new CanvasPanel();
        jTabbedPaneForOtherTools.addTab("Paint", canvasPanel);
        */
        /*
        CalculatorPanel calculatorPanel = new CalculatorPanel();
        jTabbedPaneForOtherTools.addTab("Calculator", calculatorPanel);
        TapTempoTool tapTempoTool = new TapTempoTool();
        jTabbedPaneForOtherTools.addTab("Tap Tempo", tapTempoTool);
        */

        /*
        PianoPanel pianoPanel = new PianoPanel();
        jTabbedPaneForOtherTools.addTab("Piano", pianoPanel);
         */
        /*
        DateTimePanel dateTimePanel = new DateTimePanel();
        jTabbedPaneForOtherTools.addTab("Date Diff", dateTimePanel);

         */
        /*
        CountdownTimerPanel countdownTimerPanel = new CountdownTimerPanel();
        jTabbedPaneForOtherTools.addTab("Countdown", countdownTimerPanel);
        */
        /*
        BrickBreakerGamePanel brickBreakerGamePanel = new BrickBreakerGamePanel();
        jTabbedPaneForOtherTools.addTab("Game", brickBreakerGamePanel);

         */
        /*
        SunAndMoonPanel sunAndMoonPanel = new SunAndMoonPanel();
        jTabbedPaneForOtherTools.addTab("Sun & Moon", sunAndMoonPanel);
        */




        //JTabbedPane jTabbedPaneForDeviceTesting = new JTabbedPane();
        /*
        MidiDeviceTestPanel midiInstrumentPanel = new MidiDeviceTestPanel();
        jTabbedPaneForDeviceTesting.add("MIDI test", midiInstrumentPanel);
        */
        /*
        AudioOutputPanel audioOutputPanel = new AudioOutputPanel();
        jTabbedPaneForDeviceTesting.addTab("Speaker Test", audioOutputPanel);
        */

        /*
        SystemInfoPanel systemInfoPanel = new SystemInfoPanel();
        jTabbedPaneForDeviceTesting.addTab("System Info", systemInfoPanel);
        */
        //tabbedPanel.addTab("Device Tests", jTabbedPaneForDeviceTesting);


        JTabbedPane applicationSettingsPanel = new JTabbedPane();

        // basla flatpak bilgi: flatpak tema

        ThemeSelectorPanel themeSelectorPanel = new ThemeSelectorPanel(this);


        applicationSettingsPanel.addTab("Theme", themeSelectorPanel);

        // bitti flatpak




        JPanel globalOptionsPanel = new JPanel();

        globalOptionsPanel.add(toggleAlwaysOnTopButton);

        /* TO DO in the future
        toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

        int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
        isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
        toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        globalOptionsPanel.add(toggleHistoryLoggingButton);
        */

        JButton resetFrameResolutionButton = new JButton("Reset Window Dimension");
        resetFrameResolutionButton.addActionListener(e -> resetFrameResolution());
        globalOptionsPanel.add(resetFrameResolutionButton);

        toggleAlwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop());


        // Global Mute/Unmute butonu
        globalMuteButton = new JButton("Global Mute");
        globalMuteButton.addActionListener(e -> toggleGlobalMute(globalMuteButton));

        globalOptionsPanel.add(globalMuteButton);


        applicationSettingsPanel.addTab("Window", globalOptionsPanel);
        /*
        ConfigurationEditorPanel configurationEditorPanel = new ConfigurationEditorPanel();
        applicationSettingsPanel.addTab("Config", configurationEditorPanel);
        */

        tabbedPanel.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);

        // AboutPanel aboutPanel = new AboutPanel();
        String aboutPanelTitle = "plugin.about.title";
        String aboutPanelJarLocation = "core/mtp-about-ext-1.0-SNAPSHOT.jar";
        JPanel aboutPanel = PluginLoader.loadSpecificPanel(
                aboutPanelJarLocation
                , aboutPanelTitle);
        tabbedPanel.addTab(bundle.getString(aboutPanelTitle), aboutPanel);

        Integer tabIndex = getTabIndex(aboutPanelTitle, tabbedPanel);
        if(tabIndex != null) {
            tabbedPanel.setTabComponentAt(tabIndex, createTabHeader(tabbedPanel, tabIndex));
        }

        add(tabbedPanel, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.




/*
        soundControllers.add(playerPanel);
        soundControllers.add(playerPanel2);
        soundControllers.add(playerPanel3);
*/
        //soundControllers.add(binauralPanel);
        //soundControllers.add(noisePanel);

        // TODO soundControllers.add(metronomePanel);

        soundControllers.add(pomodoroApp.getTickSoundPanel());
        //soundControllers.add(pomodoroApp.getTickSoundMidiPanel());

        soundControllers.add(pomodoroApp.getEndingSoundPanel());


        // soundControllers.add(pomodoroPanel); // tick sound + ending sound
        // piano, speaker test, device test

    }

    private void resetFrameResolution() {
        setSize(defaultFrameDimension);
    }


    private void toggleHistoryLogging() {
        isHistoryLoggingEnabled = !isHistoryLoggingEnabled;
        if (isHistoryLoggingEnabled) {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.on"));
        } else {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.off"));
        }
    }

    private void toggleAlwaysOnTop() {
        boolean isSelected = toggleAlwaysOnTopButton.isSelected();
        setAlwaysOnTop(isSelected);
    }

    public String translate(String key) {
        return bundle.getString(key);
    }

    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
    }


    private static boolean isGlobalMuted = false;
    private static final java.util.List<SoundController> soundControllers = new ArrayList<>();
    private static final List<Boolean> previousMuteStates = new ArrayList<>();

    private void toggleGlobalMute(JButton button) {
        if (isGlobalMuted) {
            // Global Unmute
            for (int i = 0; i < soundControllers.size(); i++) {
                if (!previousMuteStates.get(i)) { // Eski durumu kontrol et
                    soundControllers.get(i).unmute();
                }
            }
            isGlobalMuted = false;
            button.setText("Global Mute");
            System.out.println(this.prepareGlobalSoundReport());
        } else {
            // Global Mute
            previousMuteStates.clear();
            for (SoundController controller : soundControllers) {
                previousMuteStates.add(controller.isMuted());
                controller.mute();
            }
            isGlobalMuted = true;
            button.setText("Global Unmute");
        }
    }

    private String prepareGlobalSoundReport() {
        StringBuilder sb = new StringBuilder();


        return sb.toString();
    }


    /**
     * Sekme başlığını özel bir panel olarak oluşturur (Label + Mute Button).
     */
    private JPanel createTabHeader(JTabbedPane tabbedPane, int tabIndex) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        panel.setOpaque(false); // Arka planı şeffaf yap

        // Sekme başlığı (Tab adı)
        JLabel titleLabel = new JLabel(tabbedPane.getTitleAt(tabIndex));
        // titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1)); // Butonla aralık bırak

        // Mute/Unmute butonu
        muteButtonAtTab = new JButton("🔊"); // Varsayılan: Ses açık
        muteButtonAtTab.setBorderPainted(false);
        muteButtonAtTab.setContentAreaFilled(false);
        muteButtonAtTab.setFocusPainted(false);
        muteButtonAtTab.setOpaque(false);
        muteButtonAtTab.setToolTipText("Mute/Unmute");

        muteButtonAtTab.addActionListener(e -> toggleGlobalMuteAtTab());


        /* todo
        // Butona basılınca ikon değişecek
        muteButton.addActionListener(new ActionListener() {
            private boolean isMuted = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                isMuted = !isMuted;
                muteButton.setText(isMuted ? "🔇" : "🔊"); // 🔊 Ses Açık, 🔇 Ses Kapalı
                // System.out.println("Tab " + (tabIndex + 1) + " " + (isMuted ? "Muted" : "Unmuted"));
            }
        });
        */

        // Panel içine label ve butonu ekleyelim
        panel.add(titleLabel);
        panel.add(muteButtonAtTab);

        return panel;
    }


    private void toggleGlobalMuteAtTab() {
        if (isGlobalMuted) {
            // Global Unmute
            for (int i = 0; i < soundControllers.size(); i++) {
                if (!previousMuteStates.get(i)) { // Eski durumu kontrol et
                    soundControllers.get(i).unmute();
                }
            }
            isGlobalMuted = false;
            //muteButtonAtTab.setText("Global Mute");
            muteButtonAtTab.setText("🔊"); // 🔊 Ses Açık, 🔇 Ses Kapalı

            System.out.println(this.prepareGlobalSoundReport());
        } else {
            // Global Mute
            previousMuteStates.clear();
            for (SoundController controller : soundControllers) {
                previousMuteStates.add(controller.isMuted());
                controller.mute();
            }
            isGlobalMuted = true;
            //muteButtonAtTab.setText("Global Unmute");
            muteButtonAtTab.setText("🔇"); // 🔊 Ses Açık, 🔇 Ses Kapalı

        }
    }



    /**
     * JTabbedPane içinde belirli bir sekmenin indeksini döndürür.
     *
     * @param tabNameKey  messages.properties dosyasındaki sekme başlığının anahtarı
     * @param tabbedPane  JTabbedPane nesnesi
     * @return           Sekmenin indeksini döndürür, bulunamazsa null döndürür
     */
    public Integer getTabIndex(String tabNameKey, JTabbedPane tabbedPane) {
        // Çeviri dosyasından sekmenin görünen adını al
        String translatedTabName = bundle.getString(tabNameKey);

        // Sekme başlıklarını kontrol et
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (translatedTabName.equals(tabbedPane.getTitleAt(i))) {
                return i;  // İlk eşleşmeyi döndür
            }
        }
        return null; // Sekme bulunamazsa null döndür
    }


}

