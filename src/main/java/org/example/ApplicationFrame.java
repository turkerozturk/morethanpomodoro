package org.example;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.example.initial.jpanels.sound.controller.SoundController;
import org.example.jpanels.about.AboutPanel;
import org.example.jpanels.binaural.BinauralPanel;
import org.example.jpanels.calculator.CalculatorPanel;
import org.example.jpanels.datetime.DateTimePanel;
import org.example.jpanels.metronome.MetronomePanel;
import org.example.jpanels.mididevice.MidiInstrumentPanel;
import org.example.jpanels.mp3.Mp3PlayerFx;
import org.example.jpanels.noisegenerator.NoisePanel;
import org.example.jpanels.notes.NotesPanel;
import org.example.jpanels.paint.CanvasPanel;
import org.example.jpanels.piano.PianoPanel;
import org.example.jpanels.pomodoro.PomodoroMainPanel;
import org.example.jpanels.speakertest.AudioOutputPanel;
import org.example.jpanels.taptempo.TapTempoTool;
import org.example.newpomodoro.PomodoroAppPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationFrame extends JFrame {

    private final Mp3PlayerFx playerPanel;
    private final Mp3PlayerFx playerPanel2;
    private final Mp3PlayerFx playerPanel3;
    private JButton globalMuteButton;

    private JButton muteButtonAtTab;

    private int frameWidth, frameHeight;

    private static final String[] themes = {
            //"FlatLightLaf", "FlatDarkLaf", "FlatIntelliJLaf", "FlatDarculaLaf",
            "Swing (Metal)",
            "FlatArcIJTheme", "FlatArcOrangeIJTheme", "FlatArcDarkIJTheme", "FlatArcDarkOrangeIJTheme",
            "FlatCarbonIJTheme", "FlatCobalt2IJTheme", "FlatCyanLightIJTheme", "FlatDarkFlatIJTheme",
            "FlatDarkPurpleIJTheme", "FlatDraculaIJTheme", "FlatGradiantoDarkFuchsiaIJTheme",
            "FlatGradiantoDeepOceanIJTheme", "FlatGradiantoMidnightBlueIJTheme",
            "FlatGradiantoNatureGreenIJTheme", "FlatGrayIJTheme", "FlatGruvboxDarkHardIJTheme",
            "FlatGruvboxDarkMediumIJTheme", "FlatGruvboxDarkSoftIJTheme", "FlatHiberbeeDarkIJTheme",
            "FlatHighContrastIJTheme", "FlatLightFlatIJTheme", "FlatMaterialDesignDarkIJTheme",
            "FlatMonocaiIJTheme", "FlatMonokaiProIJTheme", "FlatNordIJTheme", "FlatOneDarkIJTheme",
            "FlatSolarizedDarkIJTheme", "FlatSolarizedLightIJTheme", "FlatSpacegrayIJTheme",
            "FlatVuesionIJTheme", "FlatXcodeDarkIJTheme", "FlatAtomOneDarkIJTheme",
            "FlatAtomOneLightIJTheme", "FlatGitHubIJTheme", "FlatGitHubDarkIJTheme",
            "FlatLightOwlIJTheme", "FlatMaterialDarkerIJTheme", "FlatMaterialDeepOceanIJTheme",
            "FlatMaterialLighterIJTheme", "FlatMaterialOceanicIJTheme", "FlatMaterialPalenightIJTheme",
            "FlatMoonlightIJTheme", "FlatNightOwlIJTheme"
    };


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
        setSize(frameWidth , frameHeight);
        defaultFrameDimension = getSize();
        setLocationRelativeTo(null);
    }

    public ApplicationFrame() {

        loadVariablesFromConfig();

        initializeApplicationFrame();



        toggleAlwaysOnTopButton = new JToggleButton(translate("frame.always.on.top"));
        toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

        int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
        isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
        toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);


        int alwaysOnTopAsInt = Integer.parseInt(props.getProperty("always.on.top.toggle", "1"));
        isAlwaysOnTop = (alwaysOnTopAsInt == 1);
        toggleAlwaysOnTopButton.setSelected(isAlwaysOnTop);
        if (toggleAlwaysOnTopButton.isSelected()) {
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.on"));
            setAlwaysOnTop(isAlwaysOnTop);
        } else {
            toggleAlwaysOnTopButton.setText(translate("button.always.on.top.off"));
        }


        JTabbedPane tabbedPanel = new JTabbedPane();


        PomodoroMainPanel pomodoroPanel = new PomodoroMainPanel();

        tabbedPanel.addTab("Pomodoro", pomodoroPanel);


        JTabbedPane jTabbedPaneForNoises = new JTabbedPane();

        BinauralPanel binauralPanel = new BinauralPanel();
        jTabbedPaneForNoises.addTab(translate("tab.panel.binaural.beats.title"), binauralPanel);
        NoisePanel noisePanel = new NoisePanel();
        jTabbedPaneForNoises.addTab("Noise Generator", noisePanel.getPlayerPanel());
        MetronomePanel metronomePanel = new MetronomePanel();
        jTabbedPaneForNoises.addTab("Metronome", metronomePanel.getPlayerPanel());


        tabbedPanel.addTab("Noise Generators", jTabbedPaneForNoises);


        JTabbedPane jTabbedPaneForMp3 = new JTabbedPane();
         playerPanel = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.1.file.location", "playlist1.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player", playerPanel.getPlayerPanel());
         playerPanel2 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.2.file.location", "playlist2.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player2", playerPanel2.getPlayerPanel());
         playerPanel3 = new Mp3PlayerFx(props.getProperty("mp3.playlist.number.3.file.location", "playlist3.txt"));
        jTabbedPaneForMp3.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        tabbedPanel.addTab("MP3 Players", jTabbedPaneForMp3);




        JTabbedPane jTabbedPaneForOtherTools = new JTabbedPane();


        NotesPanel notesPanel = new NotesPanel();
        jTabbedPaneForOtherTools.addTab("Notes", notesPanel);
        CanvasPanel canvasPanel = new CanvasPanel();
        jTabbedPaneForOtherTools.addTab("Paint", canvasPanel);
        CalculatorPanel calculatorPanel = new CalculatorPanel();
        jTabbedPaneForOtherTools.addTab("Calculator", calculatorPanel);
        TapTempoTool tapTempoTool = new TapTempoTool();
        jTabbedPaneForOtherTools.addTab("Tap Tempo", tapTempoTool);
        PianoPanel pianoPanel = new PianoPanel();
        jTabbedPaneForOtherTools.addTab("Piano", pianoPanel);
        DateTimePanel dateTimePanel = new DateTimePanel();
        jTabbedPaneForOtherTools.addTab("Date Diff", dateTimePanel);

        tabbedPanel.addTab("Other Tools", jTabbedPaneForOtherTools);





        JTabbedPane jTabbedPaneForDeviceTesting = new JTabbedPane();
        MidiInstrumentPanel midiInstrumentPanel = new MidiInstrumentPanel();
        jTabbedPaneForDeviceTesting.add("MIDI test", midiInstrumentPanel);
        AudioOutputPanel audioOutputPanel = new AudioOutputPanel();
        jTabbedPaneForDeviceTesting.addTab("Speaker Test", audioOutputPanel);

        tabbedPanel.addTab("Device Tests", jTabbedPaneForDeviceTesting);






        JPanel applicationSettingsPanel = new JPanel();

        applicationSettingsPanel.add(toggleAlwaysOnTopButton);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        applicationSettingsPanel.add(toggleHistoryLoggingButton);

        JButton resetFrameResolutionButton = new JButton("Reset Window Dimension");
        resetFrameResolutionButton.addActionListener(e -> resetFrameResolution());
        applicationSettingsPanel.add(resetFrameResolutionButton);





        // basla flatpak bilgi: flatpak tema

        JPanel themePanel = new JPanel();

        JLabel label = new JLabel(bundle.getString("settings.select.theme"));
        JComboBox<String> themeSelector = new JComboBox<String>(themes);

        // Seçenekler Arayüze Ekleniyor
        themePanel.add(label);
        themePanel.add(themeSelector);

        ;

        String fullClassName = UIManager.getLookAndFeel().getClass().getCanonicalName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        themeSelector.setSelectedItem(simpleClassName);

        // Buton Tıklanınca Seçili Temayı Uygula
        themeSelector.addActionListener(e -> applyTheme((String) themeSelector.getSelectedItem(),
                ApplicationFrame.this));

        applicationSettingsPanel.add(themePanel);

        // bitti flatpak







        tabbedPanel.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);

        AboutPanel aboutPanel = new AboutPanel();
        tabbedPanel.addTab("About", aboutPanel);

        tabbedPanel.setTabComponentAt(5, createTabHeader(tabbedPanel, 5));





        add(tabbedPanel, BorderLayout.CENTER); // tum hersey tabbedpanede. en son frame icine eklemis olduk.

        toggleAlwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop());


        // Global Mute/Unmute butonu
        globalMuteButton = new JButton("Global Mute");
        globalMuteButton.addActionListener(e -> toggleGlobalMute(globalMuteButton));

        applicationSettingsPanel.add(globalMuteButton);



        soundControllers.add(playerPanel);
        soundControllers.add(playerPanel2);
        soundControllers.add(playerPanel3);

        soundControllers.add(binauralPanel);
        soundControllers.add(noisePanel);
        soundControllers.add(metronomePanel);

        soundControllers.add(pomodoroPanel.getTickSoundPanel());
        soundControllers.add(pomodoroPanel.getEndingSoundPanel());


        PomodoroAppPanel pomodoroApp = new PomodoroAppPanel();
        tabbedPanel.addTab("Pomodoro", pomodoroApp);

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


    private static void applyTheme(String theme, JFrame frame) {

        Dimension oldSize = frame.getSize();


        try {
            switch (theme) {
                case "Swing (Metal)":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
                case "FlatArcIJTheme":
                    UIManager.setLookAndFeel(new FlatArcIJTheme());
                    break;
                case "FlatArcOrangeIJTheme":
                    UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
                    break;
                case "FlatArcDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatArcDarkIJTheme());
                    break;
                case "FlatArcDarkOrangeIJTheme":
                    UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());
                    break;
                case "FlatCarbonIJTheme":
                    UIManager.setLookAndFeel(new FlatCarbonIJTheme());
                    break;
                case "FlatCobalt2IJTheme":
                    UIManager.setLookAndFeel(new FlatCobalt2IJTheme());
                    break;
                case "FlatCyanLightIJTheme":
                    UIManager.setLookAndFeel(new FlatCyanLightIJTheme());
                    break;
                case "FlatDarkFlatIJTheme":
                    UIManager.setLookAndFeel(new FlatDarkFlatIJTheme());
                    break;
                case "FlatDarkPurpleIJTheme":
                    UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
                    break;
                case "FlatDraculaIJTheme":
                    UIManager.setLookAndFeel(new FlatDraculaIJTheme());
                    break;
                case "FlatGradiantoDarkFuchsiaIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoDarkFuchsiaIJTheme());
                    break;
                case "FlatGradiantoDeepOceanIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoDeepOceanIJTheme());
                    break;
                case "FlatGradiantoMidnightBlueIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoMidnightBlueIJTheme());
                    break;
                case "FlatGradiantoNatureGreenIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoNatureGreenIJTheme());
                    break;
                case "FlatGrayIJTheme":
                    UIManager.setLookAndFeel(new FlatGrayIJTheme());
                    break;
                case "FlatGruvboxDarkHardIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkHardIJTheme());
                    break;
                case "FlatGruvboxDarkMediumIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkMediumIJTheme());
                    break;
                case "FlatGruvboxDarkSoftIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkSoftIJTheme());
                    break;
                case "FlatHiberbeeDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatHiberbeeDarkIJTheme());
                    break;
                case "FlatHighContrastIJTheme":
                    UIManager.setLookAndFeel(new FlatHighContrastIJTheme());
                    break;
                case "FlatLightFlatIJTheme":
                    UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
                    break;
                case "FlatMaterialDesignDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDesignDarkIJTheme());
                    break;
                case "FlatMonocaiIJTheme":
                    UIManager.setLookAndFeel(new FlatMonocaiIJTheme());
                    break;
                case "FlatMonokaiProIJTheme":
                    UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());
                    break;
                case "FlatNordIJTheme":
                    UIManager.setLookAndFeel(new FlatNordIJTheme());
                    break;
                case "FlatOneDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
                    break;
                case "FlatSolarizedDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatSolarizedDarkIJTheme());
                    break;
                case "FlatSolarizedLightIJTheme":
                    UIManager.setLookAndFeel(new FlatSolarizedLightIJTheme());
                    break;
                case "FlatSpacegrayIJTheme":
                    UIManager.setLookAndFeel(new FlatSpacegrayIJTheme());
                    break;
                case "FlatVuesionIJTheme":
                    UIManager.setLookAndFeel(new FlatVuesionIJTheme());
                    break;
                case "FlatXcodeDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
                    break;
                case "FlatAtomOneDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatAtomOneDarkIJTheme());
                    break;
                case "FlatAtomOneLightIJTheme":
                    UIManager.setLookAndFeel(new FlatAtomOneLightIJTheme());
                    break;
                case "FlatGitHubIJTheme":
                    UIManager.setLookAndFeel(new FlatGitHubIJTheme());
                    break;
                case "FlatGitHubDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatGitHubDarkIJTheme());
                    break;
                case "FlatLightOwlIJTheme":
                    UIManager.setLookAndFeel(new FlatLightOwlIJTheme());
                    break;
                case "FlatMaterialDarkerIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDarkerIJTheme());
                    break;
                case "FlatMaterialDeepOceanIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDeepOceanIJTheme());
                    break;
                case "FlatMaterialLighterIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialLighterIJTheme());
                    break;
                case "FlatMaterialOceanicIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialOceanicIJTheme());
                    break;
                case "FlatMaterialPalenightIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialPalenightIJTheme());
                    break;
                case "FlatMonokaiProIJTheme2":
                    UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());
                    break;
                case "FlatMoonlightIJTheme":
                    UIManager.setLookAndFeel(new FlatMoonlightIJTheme());
                    break;
                case "FlatNightOwlIJTheme":
                    UIManager.setLookAndFeel(new FlatNightOwlIJTheme());
                    break;
                case "FlatSolarizedDarkIJTheme2":
                    UIManager.setLookAndFeel(new FlatSolarizedDarkIJTheme());
                    break;
                case "FlatSolarizedLightIJTheme2":
                    UIManager.setLookAndFeel(new FlatSolarizedLightIJTheme());
                    break;
                default:
                    return;
            }
        // Arayüzü güncelle
        SwingUtilities.updateComponentTreeUI(frame);

            // Pencere boyutunu eski haline getir
            frame.setSize(oldSize);
            frame.setPreferredSize(oldSize);
            frame.pack(); // UI elemanlarını günceller

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}



}
