package org.example.newpomodoro;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// PomodoroService sınıfını daha önce verdiğimiz kodla oluşturduğunuzu varsayıyoruz.
public class PomodoroApp {

    private JFrame frame;
    private JLabel remainingLabel;
    private JButton startStopButton;
    private JButton resetButton;
    private JButton nextButton;
    private JToggleButton autoPlayToggle;
    private JButton pauseButton; // İsteğe bağlı; burada stop() gibi işlev görebilir.

    private JSpinner workSpinner;
    private JSpinner shortBreakSpinner;
    private JSpinner longBreakSpinner;

    // PomodoroService nesnesi
    private PomodoroService service;

    // Swing Timer: display paneli periyodik güncellemek için
    private Timer displayUpdater;

    // Durum takibi için: Timer çalışıyorsa "stop" butonuyla durdurulmalı
    private boolean isRunning = false;

    public PomodoroApp() {
        service = new PomodoroService();
        initialize();
        startDisplayUpdater();
    }

    private void initialize() {
        frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // JSplitPane: Üst (display) ve alt (tabbed) paneller
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(100);
        splitPane.setResizeWeight(0.4);
        frame.getContentPane().add(splitPane);

        // Üst Panel: Display Panel
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());

        // Kalan süreyi gösteren etiket
        remainingLabel = new JLabel(formatTime(service.getRemainingSeconds()), SwingConstants.CENTER);
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        displayPanel.add(remainingLabel, BorderLayout.CENTER);

        // Buton paneli
        JPanel buttonPanel = new JPanel();
        startStopButton = new JButton("Start");
        resetButton = new JButton("Reset");
        nextButton = new JButton("Next");
        autoPlayToggle = new JToggleButton("AutoPlay OFF");
        pauseButton = new JButton("Pause");

        buttonPanel.add(startStopButton);
        buttonPanel.add(pauseButton);
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

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pause işlemi, burada stop ile aynı mantıkta çalışıyor.
                service.stop();
                isRunning = false;
                startStopButton.setText("Start");
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
                remainingLabel.setText(formatTime(service.getRemainingSeconds()));
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                service.next();
                // Butonlardan gelen reset sonrasında spinner'ları da güncelleyelim
                updateSpinnersFromService();
                remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                // Timer durduysa, isRunning false olarak kalır.
                isRunning = false;
                startStopButton.setText("Start");
            }
        });

        autoPlayToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = autoPlayToggle.isSelected();
                service.enableAutoPlay(selected);
                autoPlayToggle.setText(selected ? "AutoPlay ON" : "AutoPlay OFF");
            }
        });

        splitPane.setTopComponent(displayPanel);

        // Alt Panel: Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();

        // Work tab
        JPanel workPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        workPanel.add(new JLabel("Work Duration (min): "));
        workSpinner = new JSpinner(new SpinnerNumberModel(service.getWorkDurationMinutes(), 1, 120, 1));
        workPanel.add(workSpinner);
        tabbedPane.addTab("Work", workPanel);

        // Short Break tab
        JPanel shortBreakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shortBreakPanel.add(new JLabel("Short Break (min): "));
        shortBreakSpinner = new JSpinner(new SpinnerNumberModel(service.getShortBreakDurationMinutes(), 1, 60, 1));
        shortBreakPanel.add(shortBreakSpinner);
        tabbedPane.addTab("Short Break", shortBreakPanel);

        // Long Break tab
        JPanel longBreakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        longBreakPanel.add(new JLabel("Long Break (min): "));
        longBreakSpinner = new JSpinner(new SpinnerNumberModel(service.getLongBreakDurationMinutes(), 1, 120, 1));
        longBreakPanel.add(longBreakSpinner);
        tabbedPane.addTab("Long Break", longBreakPanel);

        // Spinner listener'ları:
        ChangeListener spinnerListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Aktif tab'a göre ilgili süreyi güncelle.
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == 0) { // Work tab
                    int newVal = (Integer) workSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.WORK_TIME) {
                        // Aktifse updateActiveTimerDuration kullanarak kalan süreyi de güncelleyelim
                        service.updateActiveTimerDuration(newVal);
                        remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setWorkDurationMinutes(newVal);
                    }
                } else if (selectedIndex == 1) { // Short Break tab
                    int newVal = (Integer) shortBreakSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                        service.updateActiveTimerDuration(newVal);
                        remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setShortBreakDurationMinutes(newVal);
                    }
                } else if (selectedIndex == 2) { // Long Break tab
                    int newVal = (Integer) longBreakSpinner.getValue();
                    if (service.getActiveTimerType() == PomodoroService.PomodoroTimerType.LONG_BREAK) {
                        service.updateActiveTimerDuration(newVal);
                        remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                    } else {
                        service.setLongBreakDurationMinutes(newVal);
                    }
                }
            }
        };

        workSpinner.addChangeListener(spinnerListener);
        shortBreakSpinner.addChangeListener(spinnerListener);
        longBreakSpinner.addChangeListener(spinnerListener);


        // Tab değiştiğinde, aktif timer türüyle ilgili spinner'ı göstermek amacıyla
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                // Eğer aktif timer tipi farklı ise, değiştirilmiş spinner değerinin display'e yansıması için reset çağırabilirsiniz.
                if (selectedIndex == 0 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.WORK_TIME) {
                    service.setWorkDurationMinutes((Integer) workSpinner.getValue());
                    //service.reset();
                    remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                } else if (selectedIndex == 1 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.SHORT_BREAK) {
                    service.setShortBreakDurationMinutes((Integer) shortBreakSpinner.getValue());
                    //service.reset();
                    remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                } else if (selectedIndex == 2 && service.getActiveTimerType() != PomodoroService.PomodoroTimerType.LONG_BREAK) {
                    service.setLongBreakDurationMinutes((Integer) longBreakSpinner.getValue());
                    //service.reset();
                    remainingLabel.setText(formatTime(service.getRemainingSeconds()));
                }
            }
        });


        splitPane.setBottomComponent(tabbedPane);
        frame.setVisible(true);
    }

    // Swing Timer ile display panelde kalan süreyi periyodik güncelle
    private void startDisplayUpdater() {
        displayUpdater = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingLabel.setText(formatTime(service.getRemainingSeconds()));
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

    public static void main(String[] args) {
        // Swing UI Event Dispatch Thread üzerinde çalıştır.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PomodoroApp();
            }
        });
    }
}

