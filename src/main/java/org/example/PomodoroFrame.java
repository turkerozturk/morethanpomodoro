package org.example;

import org.example.jpanels.speakertest.AudioOutputPanel;
import org.example.jpanels.metronome.MetronomePanel;
import org.example.jpanels.mididevice.MidiInstrumentPanel;
import org.example.jpanels.noisegenerator.NoisePanel;
import org.example.jpanels.mp3.Mp3PlayerFx;
import org.example.sounds.AsyncBeep;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.Timer;

public class PomodoroFrame extends JFrame {

    Properties props = new Properties();


    BinauralBeatsGenerator binauralBeatsGenerator;


    PomodoroTimerState pomodoroTimerState;


    private JLabel timeLabel;
    private JButton startButton, stopButton, resetButton, jumpToNextButton;
    JToggleButton toggleFinishSoundButton, toggleRandomTickButton, toggleAlwaysOnTopButton
            , toggleAutoPlayButton, toggleTickSoundButton, isEndingSoundMutedButton, toggleHistoryLoggingButton
    , toggleBinauralBeatsButton;

    JSlider endingSoundVolumeSlider, tickSoundVolumeSlider, sliderBinauralBeatSlider;

    JSpinner spinnerSelectMidiInstrument, spinnerSelectMidiNote, spinnerPomodoroWorkDuration, spinnerPomodoroShortBreak
            , spinnerPomodoroLongBreak, spinnerBinauralBaseFrequency, spinnerBinauralBeatFrequency;
    private Timer timer;

    private boolean isAutoPlay, isRandomTick, isAlwaysOnTop, isHistoryLoggingEnabled, isBinauralBeatsEnabled;



    // dakika
    private int pomodoroWorkDuration, pomodoroShortBreak, pomodoroLongBreak;
    private int metronomeInterval; // saniye
    private String soundType;
    private String soundFile;

    int midiInstrument, midiNote, midiVolume;

    int wavSoundVolume, frequencySoundVolume;

    private int binauralBaseFrequency, binauralBeatFrequency;

    private String currentTimerLogMessage;

    private int remainingSeconds;
    private boolean toggleWorkSession = true;
    private int pomodoroCount = 0; // onemli. 0 olarak kalsin.


    private final MetronomePlayer metronomePlayer;

    private ResourceBundle bundle;
    private String language;
    private String country;
    private int endingSoundVolume;
    private int binauralBeatsVolume;


    public PomodoroFrame() {

        loadConfig();  // config.properties oku

        setTitle(translate("frame.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 250);
        setLocationRelativeTo(null);

        remainingSeconds = pomodoroWorkDuration * 60; // initial timer in minutes.





        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel pomodoroControlsPanel = new JPanel();


        pomodoroControlsPanel.add(startButton);
        pomodoroControlsPanel.add(stopButton);
        pomodoroControlsPanel.add(resetButton);
        pomodoroControlsPanel.add(jumpToNextButton);
        pomodoroControlsPanel.add(toggleAutoPlayButton);

        tabbedPane.addTab(translate("tab.panel.controls.title"), pomodoroControlsPanel);


        JPanel pomodoroTimingsPanel = new JPanel();



        SpinnerModel spinnerModel3 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroWorkDuration = new JSpinner(spinnerModel3);
        spinnerPomodoroWorkDuration.setValue((int) pomodoroWorkDuration);
        spinnerPomodoroWorkDuration.addChangeListener(e -> changePomodoroWorkDuration());
        //spinnerPomodoroWorkDuration.setPreferredSize(new Dimension(100, 40));
        JPanel panel0 = new JPanel();
        panel0.add(new JLabel(bundle.getString("spinner.pomodoro.work.duration")));
        panel0.add(spinnerPomodoroWorkDuration);
        pomodoroTimingsPanel.add(panel0);

        SpinnerModel spinnerModel4 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroShortBreak = new JSpinner(spinnerModel4);
        spinnerPomodoroShortBreak.setValue((int) pomodoroShortBreak);
        spinnerPomodoroShortBreak.addChangeListener(e -> changePomodoroShortBreak());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel1 = new JPanel();
        panel1.add(new JLabel(bundle.getString("spinner.pomodoro.short.break.duration")));
        panel1.add(spinnerPomodoroShortBreak);
        pomodoroTimingsPanel.add(panel1);


        SpinnerModel spinnerModel5 = new SpinnerNumberModel(0, 0, 9999999, 1);
        spinnerPomodoroLongBreak = new JSpinner(spinnerModel5);
        spinnerPomodoroLongBreak.setValue((int) pomodoroLongBreak);
        spinnerPomodoroLongBreak.addChangeListener(e -> changePomodoroLongBreak());
        //spinnerPomodoroLongBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel2 = new JPanel();
        panel2.add(new JLabel(bundle.getString("spinner.pomodoro.long.break.duration")));
        panel2.add(spinnerPomodoroLongBreak);
        pomodoroTimingsPanel.add(panel2);

        pomodoroTimingsPanel.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("tab.panel.timings.description")));

        tabbedPane.addTab(translate("tab.panel.timings.title"), pomodoroTimingsPanel);


        // TODO butona basınca binaural çalışsın.
        JPanel binauralBeatsPanel = new JPanel();





        binauralBeatsVolume = Integer.parseInt(props.getProperty("slider.binaural.beats.loudness"));
        sliderBinauralBeatSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, binauralBeatsVolume);
        sliderBinauralBeatSlider.setMajorTickSpacing(10);
        sliderBinauralBeatSlider.setMinorTickSpacing(1);
        sliderBinauralBeatSlider.setPaintTicks(true);
        sliderBinauralBeatSlider.setPaintLabels(true);
        sliderBinauralBeatSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.binaural.beats.loudness")));
        binauralBeatsPanel.add(sliderBinauralBeatSlider);
        sliderBinauralBeatSlider.addChangeListener(e -> changeBinauralSoundVolume());

        toggleBinauralBeatsButton = new JToggleButton(translate("button.binaural.beats.mute"));
        toggleBinauralBeatsButton.addActionListener(e -> toggleBinauralBeats());

        toggleBinauralBeatsButton.setSelected(isBinauralBeatsEnabled);





        binauralBeatsPanel.add(toggleBinauralBeatsButton);

        binauralBaseFrequency = Integer.parseInt(props.getProperty("spinner.binaural.beats.base.frequency"
                , "440"));
        SpinnerModel spinnerModel6 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBaseFrequency = new JSpinner(spinnerModel6);
        spinnerBinauralBaseFrequency.setValue((int) binauralBaseFrequency);
        spinnerBinauralBaseFrequency.addChangeListener(e -> changeBinauralBaseFrequency());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel3 = new JPanel();
        panel3.add(new JLabel(bundle.getString("spinner.binaural.beats.base.frequency")));
        panel3.add(spinnerBinauralBaseFrequency);
        binauralBeatsPanel.add(panel3);

        binauralBeatFrequency = Integer.parseInt(props.getProperty("spinner.binaural.beats.beat.frequency"
                , "5"));
        SpinnerModel spinnerModel7 = new SpinnerNumberModel(0, 0, 44100, 1);
        spinnerBinauralBeatFrequency = new JSpinner(spinnerModel7);
        spinnerBinauralBeatFrequency.setValue((int) binauralBeatFrequency);
        spinnerBinauralBeatFrequency.addChangeListener(e -> changeBinauralBeatFrequency());
        //spinnerPomodoroShortBreak.setPreferredSize(new Dimension(100, 40));
        JPanel panel4 = new JPanel();
        panel4.add(new JLabel(bundle.getString("spinner.binaural.beats.beat.frequency")));
        panel4.add(spinnerBinauralBeatFrequency);
        binauralBeatsPanel.add(panel4);

        binauralBeatsGenerator = new BinauralBeatsGenerator(binauralBaseFrequency,
                binauralBeatFrequency,frequencySoundVolume);

        //System.out.println(isBinauralBeatsEnabled);
        processBinauralBeats();

        tabbedPane.addTab(translate("tab.panel.binaural.beats.title"), binauralBeatsPanel);









        JPanel tickSoundPanel = new JPanel();

        // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/SliderDemoProject/src/components/SliderDemo.java
        tickSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, midiVolume);
        tickSoundVolumeSlider.setMajorTickSpacing(10);
        tickSoundVolumeSlider.setMinorTickSpacing(1);
        tickSoundVolumeSlider.setPaintTicks(true);
        tickSoundVolumeSlider.setPaintLabels(true);
        tickSoundVolumeSlider.addChangeListener(e -> changeTickSoundVolume());
        tickSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.tick.sound.loudness")));
        tickSoundPanel.add(tickSoundVolumeSlider);


        tickSoundPanel.add(toggleTickSoundButton);
        tickSoundPanel.add(toggleRandomTickButton);

        SpinnerModel spinnerModel1 = new SpinnerNumberModel(0, 0, 100, 1);
        spinnerSelectMidiInstrument = new JSpinner(spinnerModel1);
        spinnerSelectMidiInstrument.setValue((int) midiInstrument);
        spinnerSelectMidiInstrument.setPreferredSize(new Dimension(100, 40)); // Genişlik 80, yükseklik 25
        spinnerSelectMidiInstrument.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("spinner.tick.sound.instrument")));
        spinnerSelectMidiInstrument.addChangeListener(e -> changeTickSoundMidiInstrument());
        tickSoundPanel.add(spinnerSelectMidiInstrument);

        SpinnerModel spinnerModel2 = new SpinnerNumberModel(0, 0, 100, 1);
        spinnerSelectMidiNote = new JSpinner(spinnerModel2);
        spinnerSelectMidiNote.setValue((int) midiNote);
        spinnerSelectMidiNote.setPreferredSize(new Dimension(100, 40)); // Genişlik 80, yükseklik 25
        spinnerSelectMidiNote.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("spinner.tick.sound.note")));
        spinnerSelectMidiNote.addChangeListener(e -> changeTickSoundMidiNote());
        tickSoundPanel.add(spinnerSelectMidiNote);

        tabbedPane.addTab(translate("tab.panel.tick.sound.title"), tickSoundPanel);

        JPanel endingSoundPanel = new JPanel();

        endingSoundVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, frequencySoundVolume);
        endingSoundVolumeSlider.setMajorTickSpacing(10);
        endingSoundVolumeSlider.setMinorTickSpacing(1);
        endingSoundVolumeSlider.setPaintTicks(true);
        endingSoundVolumeSlider.setPaintLabels(true);
        //Font font = new Font("Serif", Font.ITALIC, 15);
        //endingSoundVolumeSlider.setFont(font);
        endingSoundVolumeSlider.setBorder(
                BorderFactory.createTitledBorder(bundle.getString("slider.ending.sound.loudness")));

        isEndingSoundMutedButton = new JToggleButton(translate("button.ending.sound.mute"));

        // TODO butonu class degiskeni yap
        endingSoundPanel.add(isEndingSoundMutedButton);
        endingSoundPanel.add(endingSoundVolumeSlider);
        endingSoundPanel.add(toggleFinishSoundButton);

        tabbedPane.addTab(translate("tab.panel.ending.sound.title"), endingSoundPanel);



        JPanel applicationSettingsPanel = new JPanel();

        applicationSettingsPanel.add(toggleAlwaysOnTopButton);

        toggleHistoryLoggingButton.addActionListener(e -> toggleHistoryLogging());
        applicationSettingsPanel.add(toggleHistoryLoggingButton);

        tabbedPane.addTab(translate("tab.panel.settings.title"), applicationSettingsPanel);




        Mp3PlayerFx playerPanel = new Mp3PlayerFx("playlist1.txt");
        tabbedPane.addTab("MP3 Player", playerPanel.getPlayerPanel());

        Mp3PlayerFx playerPanel2 = new Mp3PlayerFx("playlist2.txt");
        tabbedPane.addTab("MP3 Player2", playerPanel2.getPlayerPanel());

        Mp3PlayerFx playerPanel3 = new Mp3PlayerFx("playlist3.txt");
        tabbedPane.addTab("MP3 Player3", playerPanel3.getPlayerPanel());

        NoisePanel noisePanel = new NoisePanel();
        tabbedPane.addTab("Noise Generator", noisePanel.getPlayerPanel());

        MetronomePanel metronomePanel = new MetronomePanel();
        tabbedPane.add("Metronome", metronomePanel.getPlayerPanel());

        MidiInstrumentPanel midiInstrumentPanel = new MidiInstrumentPanel();
        tabbedPane.add("MIDI test", midiInstrumentPanel);

        AudioOutputPanel audioOutputPanel = new AudioOutputPanel();
        tabbedPane.addTab("Speaker Test", audioOutputPanel);


        // BURADAN asagisi frame in ust kismindaki sayac panalei. Sonra da JSplitepane ile ustteki ve alttaki ekleniyor.


        JPanel panelForTimer = new JPanel();

        timeLabel = new JLabel(formatTime(remainingSeconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        panelForTimer.add(timeLabel, BorderLayout.CENTER);

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelForTimer, tabbedPane);

        add(jSplitPane, BorderLayout.CENTER);

        // Metronom ayarla
        metronomePlayer = new MetronomePlayer(metronomeInterval, soundType, soundFile, true);
        metronomePlayer.setRandomEnabled(isRandomTick);
        metronomePlayer.setMidiVolume(midiVolume);
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument,midiNote);
        metronomePlayer.setWavSoundVolume(wavSoundVolume);

        // Geri sayım
        timer = new Timer(1000, (ActionEvent e) -> {
            remainingSeconds--;
            timeLabel.setText(formatTime(remainingSeconds));

            // Her saniyede metronom kontrolü
            metronomePlayer.tick();

            if (remainingSeconds <= 0) {
                stopTimer();
                if(toggleFinishSoundButton.isSelected()) {
                    playFrequencyBeep();
                }
                cycleNext();
                if(isAutoPlay) {
                    appendMessageToHistory(getCurrentTimestamp() + "\tautoplay");
                    startTimer();
                } else {
                    // System.out.println("auto play disabled");
                }
            } else {
                // System.out.println(getCurrentTimerLogMessage()); // debug icin
            }
        });



        startButton.addActionListener(e -> startTimer());
        stopButton.addActionListener(e -> stopTimer());
        resetButton.addActionListener(e -> resetTimer());
        toggleTickSoundButton.addActionListener(e -> muteUnmute());
        jumpToNextButton.addActionListener(e -> jumpToNextTimer());
        toggleFinishSoundButton.addActionListener(e -> toggleFinishSound());
        toggleRandomTickButton.addActionListener(e -> toggleRandomTick());
        toggleAlwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop());
        toggleAutoPlayButton.addActionListener(e -> toggleAutoPlay());
        endingSoundVolumeSlider.addChangeListener(e -> changeEndingSoundVolume());



    }

    private void changeBinauralBeatFrequency() {
        binauralBeatFrequency = (int) spinnerBinauralBeatFrequency.getValue();
        binauralBeatsGenerator.setBeatFrequency(binauralBeatFrequency);
    }


    private void changeBinauralBaseFrequency() {
        binauralBaseFrequency = (int) spinnerBinauralBaseFrequency.getValue();
        binauralBeatsGenerator.setBaseFrequency(binauralBaseFrequency);
    }

    private void changeBinauralSoundVolume() {
        binauralBeatsVolume = (int) (sliderBinauralBeatSlider.getValue());
        binauralBeatsGenerator.setVolume((int) (sliderBinauralBeatSlider.getValue()));

    }


    private void toggleBinauralBeats() {
        isBinauralBeatsEnabled = !isBinauralBeatsEnabled;
        processBinauralBeats();
    }

    private void processBinauralBeats() {
        if (isBinauralBeatsEnabled) {
            toggleBinauralBeatsButton.setText(translate("button.binaural.beats.mute"));
            binauralBeatsGenerator.start();

        } else {
            toggleBinauralBeatsButton.setText(translate("button.binaural.beats.unmute"));
            binauralBeatsGenerator.stop();
        }
    }

    private void toggleHistoryLogging() {
        isHistoryLoggingEnabled = !isHistoryLoggingEnabled;
        if (isHistoryLoggingEnabled) {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.on"));
        } else {
            toggleHistoryLoggingButton.setText(translate("button.logging.history.off"));
        }
    }


    private void changePomodoroWorkDuration() {
        int oldPomodoroWorkDuration = pomodoroWorkDuration;
        pomodoroWorkDuration = (Integer) spinnerPomodoroWorkDuration.getValue();
        setRemainingSeconds(oldPomodoroWorkDuration, pomodoroWorkDuration, PomodoroTimerState.WORK_TIME);
    }

    private void changePomodoroShortBreak() {
        int oldPomodoroShortBreak = pomodoroShortBreak;
        pomodoroShortBreak = (Integer) spinnerPomodoroShortBreak.getValue();
        setRemainingSeconds(oldPomodoroShortBreak, pomodoroShortBreak, PomodoroTimerState.SHORT_BREAK);
    }

    private void changePomodoroLongBreak() {
        int oldPomodoroLongBreak = pomodoroLongBreak;
        pomodoroLongBreak = (Integer) spinnerPomodoroLongBreak.getValue();
        setRemainingSeconds(oldPomodoroLongBreak, pomodoroLongBreak, PomodoroTimerState.LONG_BREAK);
    }

    private void changeTickSoundMidiNote() {
        int value = (Integer) spinnerSelectMidiNote.getValue();
        metronomePlayer.setMidiInstrumentAndNote(midiInstrument, value);

    }

    public void setRemainingSeconds(int oldDurationAsMinutes, int newDurationAsMinutes, PomodoroTimerState timerState) {
        int differenceAsSeconds = (oldDurationAsMinutes - newDurationAsMinutes) * 60;
        if(pomodoroTimerState == null) {
            pomodoroTimerState = timerState;

        }
        if(pomodoroTimerState.equals(timerState)) {
            if (remainingSeconds - differenceAsSeconds > 1) {
                remainingSeconds = remainingSeconds - differenceAsSeconds;
            }
        }
    }

    private void changeTickSoundMidiInstrument() {
        int value = (Integer) spinnerSelectMidiInstrument.getValue();
        metronomePlayer.setMidiInstrumentAndNote(value, midiNote);

        //label.setText("Seçilen Değer: " + value);
    }

    public void playFrequencyBeep() {
        //  System.out.println("Sinüs dalgası çalıyor...");
        // System.out.println("PomodoroFrame frequencySoundVolume: " + frequencySoundVolume);
        AsyncBeep.generateToneAsync(1000, 2000, false, frequencySoundVolume);
        // System.out.println("Kare dalga çalıyor...");
        //AsyncBeep.generateToneAsync(1000, 6000, true, frequencySoundVolume);

    }

    public void changeTickSoundVolume() {
        if (!tickSoundVolumeSlider.getValueIsAdjusting()) {
            midiVolume = tickSoundVolumeSlider.getValue();
            metronomePlayer.setMidiVolume(midiVolume);
            metronomePlayer.tick();
        }
    }


    public void changeEndingSoundVolume() {
        if (!endingSoundVolumeSlider.getValueIsAdjusting()) {
            frequencySoundVolume = endingSoundVolumeSlider.getValue();
            playFrequencyBeep();
        }
    }



    private void toggleAutoPlay() {

        if (toggleAutoPlayButton.isSelected()) {
            isAutoPlay = true;
            toggleAutoPlayButton.setText(translate("autoplay.on"));
        } else {
            isAutoPlay = false;
            toggleAutoPlayButton.setText(translate("autoplay.off"));
        }

    }

    private void toggleAlwaysOnTop() {
        boolean isSelected = toggleAlwaysOnTopButton.isSelected();
        setAlwaysOnTop(isSelected);
    }



    private void toggleRandomTick() {

        if (toggleRandomTickButton.isSelected()) {
            metronomePlayer.setRandomEnabled(true);
            toggleRandomTickButton.setText(translate("random.tick.on"));
        } else {
            metronomePlayer.setRandomEnabled(false);
            toggleRandomTickButton.setText(translate("random.tick.off"));
        }
    }

    private void toggleFinishSound() {


        if (toggleFinishSoundButton.isSelected()) {
            toggleFinishSoundButton.setText(translate("timer.end.sound.on"));
        } else {
            toggleFinishSoundButton.setText(translate("timer.end.sound.off"));
        }


    }

    private void jumpToNextTimer() {
        stopTimer();

        cycleNext();


    }

    private boolean isMuted = false;

    private void muteUnmute() {
        isMuted = !isMuted;
        metronomePlayer.setMuted(isMuted);
        // todo
        toggleTickSoundButton.setText(isMuted ? translate("mute.ticks") : translate("unmute.ticks"));
    }


    private void loadConfig() {




        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);

                language = props.getProperty("language.locale", "en");
                country = props.getProperty("language.country", "EN");

                Locale locale = new Locale(language, country);
                bundle = ResourceBundle.getBundle("messages", locale);

                startButton = new JButton(translate("timer.start"));
                stopButton = new JButton(translate("timer.stop"));
                resetButton = new JButton(translate("timer.reset"));
                jumpToNextButton = new JButton(translate("timer.next"));
                toggleTickSoundButton = new JToggleButton(translate("sound.tick.mute.unmute"));
                toggleAutoPlayButton = new JToggleButton(translate("autoplay.on"));
                toggleFinishSoundButton = new JToggleButton(translate("button.ending.sound.initial"));
                toggleRandomTickButton = new JToggleButton(translate("random.tick.on"));
                toggleAlwaysOnTopButton = new JToggleButton(translate("frame.always.on.top"));
                toggleHistoryLoggingButton = new JToggleButton(translate("button.logging.history.initial"));

                int historyLoggingAsInt = Integer.parseInt(props.getProperty("logging.history.toggle", "0"));
                isHistoryLoggingEnabled = (historyLoggingAsInt == 1);
                toggleHistoryLoggingButton.setSelected(isHistoryLoggingEnabled);

                pomodoroWorkDuration = Integer.parseInt(props.getProperty("work.duration", "25"));
                pomodoroShortBreak = Integer.parseInt(props.getProperty("short.break", "5"));
                pomodoroLongBreak = Integer.parseInt(props.getProperty("long.break", "15"));
                metronomeInterval = Integer.parseInt(props.getProperty("metronome.interval", "1"));
                soundType = props.getProperty("sound.type", "WAV");
                soundFile = props.getProperty("sound.file", "beep.wav");

                // = 9; // percussion, drums, bells etc.
                midiInstrument = Integer.parseInt(props.getProperty("sound.midi.instrument", "9")); // default: 9 (Percussion)
                midiNote = Integer.parseInt(props.getProperty("sound.midi.note", "60")); // default: 60 (Middle C)
                midiVolume = Integer.parseInt(props.getProperty("sound.midi.volume", "100")); // default volume level: 100

                wavSoundVolume = Integer.parseInt(props.getProperty("sound.wav.volume", "100")); // default volume level: 100

                frequencySoundVolume = Integer.parseInt(props.getProperty("sound.frequency.volume", "100"));

                endingSoundVolume = Integer.parseInt(props.getProperty("sound.ending.volume", "100"));


                int autoPlayAsInt = Integer.parseInt(props.getProperty("pomodoro.autoplay.toggle", "1"));
                isAutoPlay = (autoPlayAsInt == 1);
                toggleAutoPlayButton.setSelected(isAutoPlay);
                toggleAutoPlay();

                int randomTickAsInt = Integer.parseInt(props.getProperty("pomodoro.sound.random.tick.toggle", "1"));
                isRandomTick = (randomTickAsInt == 1);
                toggleRandomTickButton.setSelected(isRandomTick);
                if (toggleRandomTickButton.isSelected()) {
                    toggleRandomTickButton.setText(translate("random.tick.on"));
                } else {
                    toggleRandomTickButton.setText(translate("random.tick.off"));
                }

                toggleFinishSoundButton.setSelected(true);

                int alwaysOnTopAsInt = Integer.parseInt(props.getProperty("always.on.top.toggle", "1"));
                isAlwaysOnTop = (alwaysOnTopAsInt == 1);
                toggleAlwaysOnTopButton.setSelected(isAlwaysOnTop);
                if (toggleAlwaysOnTopButton.isSelected()) {
                    toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.on"));
                    setAlwaysOnTop(isAlwaysOnTop);
                } else {
                    toggleAlwaysOnTopButton.setText(translate("button.alway.on.top.off"));
                }

                int binauralBetasAsInt = Integer.parseInt(props.getProperty("button.binaural.beats.mute", "1"));
                isBinauralBeatsEnabled = (binauralBetasAsInt == 1);



            } else {
               // setDefaultValues();
            }
        } catch (IOException e) {
          //  setDefaultValues();
        }
    }

    /* config properties zaten jar icerisinde de olacagi icin buna gerek yok.
    private void setDefaultValues() {
        pomodoroWorkDuration = 25;
        pomodoroShortBreak = 5;
        pomodoroLongBreak = 15;
        metronomeInterval = 1;
        soundType = "WAV";
        soundFile = "beep.wav";
    }
    */

    private void startTimer() {
        if (!timer.isRunning()) {
           // currentTimerLogMessage = getCurrentTimestamp() + "\t" + (pomodoroCount +1) + "\t" +
            //         pomodoroWorkDuration + "\t" + toggleWorkSession;

            if(isHistoryLoggingEnabled) {
                appendMessageToHistory(getCurrentTimerLogMessage());
            }

            this.setTitle(translate("frame.title") + " - " + getCurrentTimerLogMessage());

            timer.start();


            startButton.setText(translate("timer.start"));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            resetButton.setEnabled(false);
        }


    }

    private void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
            //pomodoroTimerState = PomodoroTimerState.STOPPED;
            startButton.setText(translate("timer.start.continue"));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            resetButton.setEnabled(true);

        }
    }

    private void resetTimer() {
        stopTimer();
        pomodoroTimerState = PomodoroTimerState.STOPPED;
        remainingSeconds = pomodoroWorkDuration * 60; // Süreyi başa al
        timeLabel.setText(formatTime(remainingSeconds));
    }

    private void cycleNext() {
        // Pomodoro turu bittiğinde mola veya yeni çalışma süresi ayarla
        if (toggleWorkSession) {
            pomodoroCount++;
            if (pomodoroCount % 4 == 0) {
                pomodoroTimerState = PomodoroTimerState.LONG_BREAK;
                remainingSeconds = pomodoroLongBreak * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroLongBreak + ", " + inWorkSession;


            } else {
                pomodoroTimerState = PomodoroTimerState.SHORT_BREAK;
                remainingSeconds = pomodoroShortBreak * 60;
                //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroShortBreak + ", " + inWorkSession;
                appendMessageToHistory(getCurrentTimerLogMessage());

            }
        } else {
            pomodoroTimerState = PomodoroTimerState.WORK_TIME;
            remainingSeconds = pomodoroWorkDuration * 60;
            //currentTimerLogMessage = getCurrentTimestamp() + ", " + pomodoroCount + ", " + pomodoroWorkDuration + ", " + inWorkSession;
            appendMessageToHistory(getCurrentTimerLogMessage());

        }
        toggleWorkSession = !toggleWorkSession;
        timeLabel.setText(formatTime(remainingSeconds));

        if(isHistoryLoggingEnabled) {
            appendMessageToHistory(getCurrentTimerLogMessage());
        }
        this.setTitle(translate("frame.title") + " - " + getCurrentTimerLogMessage());




    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getCurrentTimerLogMessage() {
        currentTimerLogMessage = String.format("%s\t%d\t%d\t%s\t%b", getCurrentTimestamp(), pomodoroCount
                , remainingSeconds, pomodoroTimerState, toggleWorkSession);

        return currentTimerLogMessage;
    }

    public String translate(String key) {
        return bundle.getString(key);
    }

    public void appendMessageToHistory(String text) {
        if (isHistoryLoggingEnabled) {
            FileUtil.appendToHistory(text);
        }
    }

}
