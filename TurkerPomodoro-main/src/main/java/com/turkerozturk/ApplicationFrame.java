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
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.turkerozturk.buttons.AlwaysOnTopButton;
import com.turkerozturk.buttons.MuteAllButton;
import com.turkerozturk.initial.*;
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
import com.turkerozturk.jpanels.GradientPanel;
import com.turkerozturk.jpanels.pomodoro.PomodoroAppPanel;
//import com.turkerozturk.jpanels.textquotes.RandomTextDisplayPanel;
import com.turkerozturk.jpanels.theme.ThemeSelectorPanel;
//import com.turkerozturk.plugin.PanelPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class ApplicationFrame extends JFrame {



    private static final Logger logger = LoggerFactory.getLogger(ApplicationFrame.class);

    public static final java.util.List<SoundController> soundControllers = new ArrayList<>();


    int iconWidth, iconHeight;



    private float opacityLevel = 1.0f; // Başlangıçta tam opak
    private boolean maximized = false;
    private Point initialClick;

/*
        private final Mp3PlayerFx playerPanel;
        private final Mp3PlayerFx playerPanel2;
        private final Mp3PlayerFx playerPanel3;
*/

    private JButton muteButtonAtTab;

    private int frameWidth, frameHeight;


    JToggleButton toggleHistoryLoggingButton;

    private boolean isHistoryLoggingEnabled;

    Dimension defaultFrameDimension;


    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    private JToggleButton toggleCompactViewButton;
    private int windowControlBarPanelHeight = 30;

    private void loadVariablesFromConfig() {
        frameWidth = Integer.parseInt(props.getProperty("frame.width", "700"));
        frameHeight = Integer.parseInt(props.getProperty("frame.height", "350"));
        iconWidth = Integer.parseInt(props.getProperty("gui.icon.width"));
        iconHeight = Integer.parseInt(props.getProperty("gui.icon.height"));

    }

    private void initializeApplicationFrame() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/mtp-app-icon.png"));
        setIconImage(icon.getImage());
        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(frameWidth, frameHeight);
        defaultFrameDimension = getSize();
        setLocationRelativeTo(null);
        enableResize(); // transparent framede cerceve olmaz. kendimiz programlariz.
    }

    JPanel mainPanel;
    GradientPanel windowControlBarPanel;

    JTabbedPane tabbedPanel;

    public ApplicationFrame() {

        locationOffset = getLocation();
        previousSize = getSize();
        previousLocation = getLocation();

        loadVariablesFromConfig();


        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(prepareTransparentFrameWithControls());

        // TODO enableResize(); // 📌 Pencerenin yeniden boyutlandırılmasını etkinleştir!


        initializeApplicationFrame();


        createAndShowGUI();


        tabbedPanel = new JTabbedPane();
        tabbedPanel.setPreferredSize(new Dimension(frameWidth - 50, frameHeight - windowControlBarPanelHeight));
        //tabbedPanel.setBackground(new Color(0, 0, 0, 10)); // RGBA (A=Alpha, şeffaflık)
        //this.setBackground(new Color(0, 0, 0, 0)); // RGBA (A=Alpha, şeffaflık)
        //mainPanel.setBackground(new Color(100, 0, 0, 30)); // RGBA (A=Alpha, şeffaflık)

        PomodoroAppPanel pomodoroApp = new PomodoroAppPanel();
        tabbedPanel.addTab("Pomodoro", pomodoroApp);

        String binauralPanelJarLocation = "core/mtp-sound-binaural-beats-ext-1.0-SNAPSHOT.jar";
        if (new File(binauralPanelJarLocation).exists()) {
            String binauralPanelTitle = "plugin.sound.binaural.beats.title";
            JPanel binauralPanel = PluginLoader.loadSpecificPanel(
                    binauralPanelJarLocation
                    , binauralPanelTitle);
            tabbedPanel.addTab(bundle.getString(binauralPanelTitle), binauralPanel);
            soundControllers.add((SoundController) binauralPanel);
        } else {
            logger.info("Core Extension Not Found: " + binauralPanelJarLocation +
                    ". You can download it from: https://github.com/turkerozturk/morethanpomodoro");

        }

        String noisePanelJarLocation = "core/mtp-sound-noise-generators-ext-1.0-SNAPSHOT.jar";
        if (new File(noisePanelJarLocation).exists()) {
            String noisePanelTitle = "plugin.sound.noise.generators.title";
            JPanel noisePanel = PluginLoader.loadSpecificPanel(
                    noisePanelJarLocation
                    , noisePanelTitle);
            tabbedPanel.addTab(bundle.getString(noisePanelTitle), noisePanel);
            soundControllers.add((SoundController) noisePanel);

        } else {
            logger.info("Core Extension Not Found: " + noisePanelJarLocation +
                    ". You can download it from: https://github.com/turkerozturk/morethanpomodoro");
        }


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

        if (loader != null) {
            Map<ExtensionCategory, JTabbedPane> tabbedPanes = new EnumMap<>(ExtensionCategory.class);
            JTabbedPane mainTabbedPane = new JTabbedPane();

            for (PanelPlugin plugin : loader) {
                String tabName = bundle.containsKey(plugin.getTabName()) ? bundle.getString(plugin.getTabName()) : plugin.getTabName();

                ExtensionCategory category = plugin.getExtensionCategory();
                if (category == null) {
                    category = ExtensionCategory.OTHER; // Null durumunda default olarak OTHER kullanılıyor
                }

                // Eğer kategoriye ait tabbedPane yoksa, ilk kez oluştur ve haritaya ekle
                tabbedPanes.computeIfAbsent(category, k -> new JTabbedPane())
                        .addTab(tabName, plugin.getPanel());
            }

            // Oluşturulmuş olan tabbedPanes'leri ana tabbedPane'e ekleyelim
            for (Map.Entry<ExtensionCategory, JTabbedPane> entry : tabbedPanes.entrySet()) {
                if (entry.getValue().getTabCount() > 0) { // Boş olmayanları ekle
                    mainTabbedPane.addTab(entry.getKey().name(), entry.getValue());
                }
            }

            tabbedPanel.addTab("Plugins", mainTabbedPane);


        } else {
            logger.info("\"extensions\" folder is empty. You can download the extensions from: https://github.com/turkerozturk/morethanpomodoro");
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

        // basla flatpak bilgi: flatpak tema

        //JTabbedPane applicationSettingsPanel = new JTabbedPane();
        //ThemeSelectorPanel themeSelectorPanel = new ThemeSelectorPanel(this);






        //applicationSettingsPanel.addTab("Theme", themeSelectorPanel);

        // bitti flatpak




        //JPanel globalOptionsPanel = new JPanel();

        //globalOptionsPanel.add(toggleAlwaysOnTopButton);

        /* TO DO in the future
        toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

        int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
        isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
        toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        globalOptionsPanel.add(toggleHistoryLoggingButton);
        */




        //applicationSettingsPanel.addTab("Window", globalOptionsPanel);
        /*
        ConfigurationEditorPanel configurationEditorPanel = new ConfigurationEditorPanel();
        applicationSettingsPanel.addTab("Config", configurationEditorPanel);
        */

        //tabbedPanel.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);

        // AboutPanel aboutPanel = new AboutPanel();
        String aboutPanelJarLocation = "core/mtp-about-ext-1.0-SNAPSHOT.jar";
        if(new File(aboutPanelJarLocation).exists()) {
            String aboutPanelTitle = "plugin.about.title";
            JPanel aboutPanel = PluginLoader.loadSpecificPanel(
                    aboutPanelJarLocation
                    , aboutPanelTitle);
            tabbedPanel.addTab(bundle.getString(aboutPanelTitle), aboutPanel);
        } else {
            logger.info("Core Extension Not Found: " + aboutPanelJarLocation +
                    ". You can download it from: https://github.com/turkerozturk/morethanpomodoro");

        }

        /*
        Integer tabIndex = getTabIndex(aboutPanelTitle, tabbedPanel);
        if(tabIndex != null) {
            tabbedPanel.setTabComponentAt(tabIndex, createTabHeader(tabbedPanel, tabIndex));
        }
        */

        mainPanel.add(tabbedPanel);

        add(mainPanel, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.


/*
        soundControllers.add(playerPanel);
        soundControllers.add(playerPanel2);
        soundControllers.add(playerPanel3);
*/
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
/*
    private void toggleAlwaysOnTop() {
        boolean isSelected = toggleAlwaysOnTopButton.isSelected();
        setAlwaysOnTop(isSelected);
    }*/

    public String translate(String key) {
        return bundle.getString(key);
    }

    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
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

       // muteButtonAtTab.addActionListener(e -> toggleGlobalMuteAtTab());


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

    /**
     * related with transparent frame
     */
    private void toggleMaximize() {
        if (maximized) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        maximized = !maximized;
    }


    /**
     * related with transparent frame
     */
    private void enableFrameDrag(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int deltaX = e.getX() - initialClick.x;
                int deltaY = e.getY() - initialClick.y;

                setLocation(thisX + deltaX, thisY + deltaY);
            }
        });
    }

    AlwaysOnTopButton toggleAlwaysOnTopButton;

    JButton resetFrameResolutionButton;
    JButton changeResolutionButton;
    JButton maximizeButton;
    /**
     * related with transparent frame
     */
    public JPanel prepareTransparentFrameWithControls() {
       // setTitle("Şeffaf Kontrol Barlı JFrame");
       // setSize(600, 400);
       // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // setLocationRelativeTo(null);


        // Kenarlıkları kaldır (tam şeffaflık için gerekli)
        setUndecorated(true);
        // Şeffaflık ayarı (0.0 tamamen görünmez, 1.0 tamamen opak)
        setOpacity(opacityLevel);

        // Üst panel (Kontrol Barı)
        windowControlBarPanel = new GradientPanel();
        windowControlBarPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        windowControlBarPanel.setPreferredSize(new Dimension(frameWidth, windowControlBarPanelHeight));
        windowControlBarPanel.setMaximumSize(new Dimension(frameWidth, windowControlBarPanelHeight));
        windowControlBarPanel.setMinimumSize(new Dimension(420, windowControlBarPanelHeight));

        //windowControlBarPanel.setBackground(new Color(50, 50, 50, 200)); // Hafif şeffaf arka plan



        // Toggle window size to compact
        toggleCompactViewButton = new JToggleButton();
        FlatSVGIcon compactViewIcon = new FlatSVGIcon("svg/compact__tabler__freeze-row.svg", iconWidth, iconHeight);
        toggleCompactViewButton.setIcon(compactViewIcon);
        toggleCompactViewButton.setFocusable(false);
        toggleCompactViewButton.setBackground(Color.white);
        toggleCompactViewButton.setToolTipText("Compact");
        toggleCompactViewButton.addActionListener(e -> toggleCompactView());
        windowControlBarPanel.add(toggleCompactViewButton);

        // Theme Selector
        JButton themeSelectorButton = new JButton();
        FlatSVGIcon themeIcon = new FlatSVGIcon("svg/theme__tabler__template.svg", iconWidth, iconHeight);
        themeSelectorButton.setIcon(themeIcon);
        themeSelectorButton.setFocusable(false);
        themeSelectorButton.setBackground(Color.white);
        themeSelectorButton.setToolTipText("Theme");
        themeSelectorButton.addActionListener(e -> {
            ThemeSelectorPanel themeSelectorPanel = new ThemeSelectorPanel(this);

            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(themeSelectorButton), // Ana pencereyi alır
                    themeSelectorPanel,
                    "Select Theme",
                    JOptionPane.PLAIN_MESSAGE
            );
        });
        windowControlBarPanel.add(themeSelectorButton);




        // Global Mute/Unmute butonu
        MuteAllButton globalMuteButton = new MuteAllButton();
        windowControlBarPanel.add(globalMuteButton);

        // Always On Top Butonu
        toggleAlwaysOnTopButton = new AlwaysOnTopButton();
        windowControlBarPanel.add(toggleAlwaysOnTopButton);

        // Şeffaflık Ayar Slider'ı
        windowControlBarPanel.add(new JLabel("opacity:"));
        JSlider opacitySlider = new JSlider(30, 100, (int) (opacityLevel * 100));
        opacitySlider.setPreferredSize(new Dimension(50, iconHeight));
        opacitySlider.addChangeListener(e -> {
            opacityLevel = opacitySlider.getValue() / 100f;
            setOpacity(opacityLevel);
        });
        windowControlBarPanel.add(opacitySlider);


        // Reset window dimension to its original config values.
        resetFrameResolutionButton = new JButton();
        FlatSVGIcon resetIcon = new FlatSVGIcon("svg/reset-resolution__tabler__refresh.svg", iconWidth, iconHeight);
        resetFrameResolutionButton.setIcon(resetIcon);
        resetFrameResolutionButton.setFocusable(false);
        resetFrameResolutionButton.setBackground(Color.white);
        resetFrameResolutionButton.setToolTipText("Reset Window Resolution");
        resetFrameResolutionButton.addActionListener(e -> resetFrameResolution());
        windowControlBarPanel.add(resetFrameResolutionButton);

        // change_resolution__opuscapita__zoom_out_map.svg
        changeResolutionButton = new JButton();
        FlatSVGIcon changeResolutionIcon = new FlatSVGIcon("svg/change_resolution__opuscapita__zoom_out_map.svg", iconWidth, iconHeight);
        changeResolutionButton.setIcon(changeResolutionIcon);
        changeResolutionButton.setFocusable(false);
        changeResolutionButton.setBackground(Color.white);
        changeResolutionButton.setToolTipText("Change Window Resolution");
        changeResolutionButton.addActionListener(e -> changeFrameResolution());
        windowControlBarPanel.add(changeResolutionButton);

        // https://stackoverflow.com/questions/6507695/how-do-i-set-the-horizontal-gap-for-just-one-part-of-a-flowlayout
        windowControlBarPanel.add(Box.createRigidArea(new Dimension(30, 0)));


        // Minimize Butonu
        JButton minimizeButton = new JButton();
        FlatSVGIcon minimizeIcon = new FlatSVGIcon("svg/minimize__tabler__window-minimize.svg", iconWidth, iconHeight);
        minimizeButton.setIcon(minimizeIcon);
        minimizeButton.setFocusable(false);
        minimizeButton.setBackground(Color.white);
        minimizeButton.setToolTipText("Minimize Window");
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        windowControlBarPanel.add(minimizeButton);

        // Maksimize / Normal Butonu
        maximizeButton = new JButton();
        FlatSVGIcon maximizeIcon = new FlatSVGIcon("svg/maximize__iconduck__maximize-2.svg", iconWidth, iconHeight);
        maximizeButton.setIcon(maximizeIcon);
        maximizeButton.setFocusable(false);
        maximizeButton.setBackground(Color.white);
        maximizeButton.setToolTipText("Maximize Window");
        maximizeButton.addActionListener(e -> toggleMaximize());
        windowControlBarPanel.add(maximizeButton);

        // Kapatma Butonu
        JButton closeButton = new JButton();
        FlatSVGIcon closeIcon = new FlatSVGIcon("svg/exit-application__opuscapita__clear.svg", iconWidth, iconHeight);
        closeButton.setIcon(closeIcon);
        closeButton.setFocusable(false);
        closeButton.setBackground(Color.white);
        closeButton.setToolTipText("Close Application");
        closeButton.addActionListener(e -> System.exit(0));
        windowControlBarPanel.add(closeButton);




        // Üst paneli sürükleyerek taşımayı sağla
        enableFrameDrag(windowControlBarPanel);

        // TODO enableResize();
        /*
        // JTabbedPane (Ana İçerik)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tab 1", new JLabel("İçerik 1"));
        tabbedPane.addTab("Tab 2", new JLabel("İçerik 2"));
        tabbedPane.addTab("Tab 3", new JLabel("İçerik 3"));

        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(windowControlBarPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

         */

        // İçeriği ekle
       // setContentPane(mainPanel);

        return windowControlBarPanel;
    }

    private Dimension previousSize;
    private Point previousLocation;

    private Point locationOffset;

    /**
     * pencereyi cok kucuk yapar
     */
    private void toggleCompactView() {
        // Farkı saklayacak değişken

        if (toggleCompactViewButton.isSelected()) {
            previousSize = getSize();
            previousLocation = getLocation();
            mainPanel.remove(tabbedPanel);

            Point locationOfCompactViewButton = toggleCompactViewButton.getLocationOnScreen();
            // previousLocation ile location arasındaki farkı hesaplayalım
            locationOffset = new Point(locationOfCompactViewButton.x - previousLocation.x, locationOfCompactViewButton.y - previousLocation.y);

            setLocation(locationOfCompactViewButton);
            setSize(new Dimension(windowControlBarPanel.getMinimumSize()));
            setAlwaysOnTop(true);
            toggleAlwaysOnTopButton.setSelected(true);
            toggleAlwaysOnTopButton.setEnabled(false);

            resetFrameResolutionButton.setEnabled(false);
            changeResolutionButton.setEnabled(false);
            maximizeButton.setEnabled(false);

        } else {
            Point locationOfCompactViewButton = toggleCompactViewButton.getLocationOnScreen();

            // newLocation'a koordinat farkını uygulayarak yeni konumu belirleyelim
            Point adjustedLocation = new Point(locationOfCompactViewButton.x - locationOffset.x, locationOfCompactViewButton.y - locationOffset.y);

            setLocation(adjustedLocation);
            setSize(previousSize);
            mainPanel.add(tabbedPanel);
            setAlwaysOnTop(false);
            toggleAlwaysOnTopButton.setSelected(false);
            toggleAlwaysOnTopButton.setEnabled(true);

            resetFrameResolutionButton.setEnabled(true);
            changeResolutionButton.setEnabled(true);
            maximizeButton.setEnabled(true);


        }


        // İçeriği yenile


        revalidate();
        repaint();
    }



    private static final int RESIZE_MARGIN = 4; // Kenarlardan kaç piksel içeride algılasın?

    private int mouseX, mouseY; // Fare konumlarını takip etmek için

    private void enableResize() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int cursorType = Cursor.DEFAULT_CURSOR;

                if (e.getX() < RESIZE_MARGIN && e.getY() < RESIZE_MARGIN) {
                    cursorType = Cursor.NW_RESIZE_CURSOR; // Sol üst köşe
                } else if (e.getX() > getWidth() - RESIZE_MARGIN && e.getY() < RESIZE_MARGIN) {
                    cursorType = Cursor.NE_RESIZE_CURSOR; // Sağ üst köşe
                } else if (e.getX() < RESIZE_MARGIN && e.getY() > getHeight() - RESIZE_MARGIN) {
                    cursorType = Cursor.SW_RESIZE_CURSOR; // Sol alt köşe
                } else if (e.getX() > getWidth() - RESIZE_MARGIN && e.getY() > getHeight() - RESIZE_MARGIN) {
                    cursorType = Cursor.SE_RESIZE_CURSOR; // Sağ alt köşe
                } else if (e.getX() < RESIZE_MARGIN) {
                    cursorType = Cursor.W_RESIZE_CURSOR; // Sol kenar
                } else if (e.getX() > getWidth() - RESIZE_MARGIN) {
                    cursorType = Cursor.E_RESIZE_CURSOR; // Sağ kenar
                } else if (e.getY() < RESIZE_MARGIN) {
                    cursorType = Cursor.N_RESIZE_CURSOR; // Üst kenar
                } else if (e.getY() > getHeight() - RESIZE_MARGIN) {
                    cursorType = Cursor.S_RESIZE_CURSOR; // Alt kenar
                }

                setCursor(Cursor.getPredefinedCursor(cursorType));
            }

            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - mouseX;
                int dy = e.getY() - mouseY;

                if (getCursor().getType() == Cursor.NW_RESIZE_CURSOR) { // Sol üst köşe
                    setBounds(getX() + dx, getY() + dy, getWidth() - dx, getHeight() - dy);
                } else if (getCursor().getType() == Cursor.NE_RESIZE_CURSOR) { // Sağ üst köşe
                    setBounds(getX(), getY() + dy, getWidth() + dx, getHeight() - dy);
                } else if (getCursor().getType() == Cursor.SW_RESIZE_CURSOR) { // Sol alt köşe
                    setBounds(getX() + dx, getY(), getWidth() - dx, getHeight() + dy);
                } else if (getCursor().getType() == Cursor.SE_RESIZE_CURSOR) { // Sağ alt köşe
                    setSize(getWidth() + dx, getHeight() + dy);
                } else if (getCursor().getType() == Cursor.W_RESIZE_CURSOR) { // Sol kenar
                    setBounds(getX() + dx, getY(), getWidth() - dx, getHeight());
                } else if (getCursor().getType() == Cursor.E_RESIZE_CURSOR) { // Sağ kenar
                    setSize(getWidth() + dx, getHeight());
                } else if (getCursor().getType() == Cursor.N_RESIZE_CURSOR) { // Üst kenar
                    setBounds(getX(), getY() + dy, getWidth(), getHeight() - dy);
                } else if (getCursor().getType() == Cursor.S_RESIZE_CURSOR) { // Alt kenar
                    setSize(getWidth(), getHeight() + dy);
                }

                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
    }





    public void changeFrameResolution() {
        Window window = SwingUtilities.getWindowAncestor(toggleCompactViewButton);
        if (window == null) return;

        // Mevcut pencere boyutlarını al
        int currentWidth = window.getWidth();
        int currentHeight = window.getHeight();

        // Dialog oluştur
        JDialog dialog = new JDialog(this, "Change Resolution", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(window);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Genişlik Spinner
        dialog.add(new JLabel("Width:"), gbc);
        gbc.gridx = 1;
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(currentWidth, 100, 5000, 10));
        dialog.add(widthSpinner, gbc);

        // Yükseklik Spinner
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Height:"), gbc);
        gbc.gridx = 1;
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(currentHeight, 100, 5000, 10));
        dialog.add(heightSpinner, gbc);

        // Apply ve Cancel butonları
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel();
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");

        applyButton.addActionListener(e -> {
            int newWidth = (int) widthSpinner.getValue();
            int newHeight = (int) heightSpinner.getValue();
            window.setSize(newWidth, newHeight);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }



    private static TrayIcon trayIcon;

    public void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            logger.info("System tray is not supported!");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        //Image image = Toolkit.getDefaultToolkit().getImage("icon.png"); // İkon dosyanı buraya koy
        ImageIcon icon = new ImageIcon(getClass().getResource("/mtp-app-icon.png"));

        PopupMenu popupMenu = new PopupMenu();

        // Exit seçeneğini ekleyelim ve metni değiştirelim
        MenuItem exitItem = new MenuItem("Exit Application"); // Eski "Close Window" yerine
        exitItem.addActionListener(e -> System.exit(0));

        // Yeni Menü Item: Reset Frame Resolution
        MenuItem resetItem = new MenuItem("Reset Frame Resolution & Align Center");
        resetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleCompactViewButton.isSelected()) {
                    toggleCompactViewButton.setSelected(false);
                }
                toggleCompactView();

                resetFrameResolution();


                // Ekran boyutlarını al
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                // Pencere boyutlarını al (Eğer JFrame'in adını frame olarak varsayarsak)
                Dimension frameSize = getSize();

                // Yeni konumu hesapla (Ekran ortasına al)
                int centerX = (screenSize.width - frameSize.width) / 2;
                int centerY = (screenSize.height - frameSize.height) / 2;

                // Pencerenin yeni konumunu ayarla
                setLocation(centerX, centerY);
            }

        });

        // Menüye elemanları ekleyelim
        popupMenu.add(resetItem);
        popupMenu.add(exitItem);

        trayIcon = new TrayIcon(icon.getImage(), "MoreThnaPomodoro", popupMenu);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }



}

