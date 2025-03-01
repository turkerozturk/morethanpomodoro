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

import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.management.ManagementFactory;


import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.ExtensionCategory;
import com.turkerozturk.initial.LanguageManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class SystemInfoPanel extends JPanel implements PanelPlugin {

    private JLabel javaVersionLabel;
    private JLabel osLabel;
    private JLabel archLabel;
    private JLabel memoryLabel;
    private JLabel systemMemoryLabel;
    private JLabel cpuLabel;
    private JLabel processorLabel;
    private JLabel soundOutputLabel;
    private JLabel resolutionLabel;
    private JLabel displayInfoLabel;
    private Timer timer;

    // İşletim sistemi CPU kullanım bilgilerini almak için:
    private OperatingSystemMXBean osBean;


    private static final ConfigManager props = ConfigManager.getInstance();

    private final LanguageManager bundle = LanguageManager.getInstance();

    public SystemInfoPanel() {


        setLayout(new GridLayout(10, 1, 1, 1));

        // Sistem bilgilerini alalım
        javaVersionLabel = new JLabel(bundle.getString("system.info.java.version") + ": " + System.getProperty("java.version"));
        osLabel = new JLabel(bundle.getString("system.info.os") + ": " + System.getProperty("os.name") + " "
                + System.getProperty("os.version"));
        archLabel = new JLabel(bundle.getString("system.info.arch") + ": " + System.getProperty("os.arch"));
        memoryLabel = new JLabel(bundle.getString("system.info.memory") + ": ");
        systemMemoryLabel = new JLabel(bundle.getString("system.info.system.memory") + ": ");
        cpuLabel = new JLabel(bundle.getString("system.info.cpu.usage") + ": ");
        processorLabel = new JLabel(bundle.getString("system.info.cpu.count") + ": " + Runtime.getRuntime().availableProcessors());
        soundOutputLabel = new JLabel(bundle.getString("system.info.sound.output") + ": " + getDefaultSoundOutput());
        resolutionLabel = new JLabel();
        displayInfoLabel = new JLabel();

        // Label'ları panele ekleyelim

        add(javaVersionLabel);
        add(resolutionLabel);
        add(osLabel);
        add(archLabel);
        add(memoryLabel);
        add(systemMemoryLabel);
        add(cpuLabel);
        add(processorLabel);
        add(soundOutputLabel);
        add(displayInfoLabel);
        // İlk çözünürlüğü ayarla
        updateResolutionLabel();

        // Panel yeniden boyutlandırıldığında çözünürlüğü güncelle
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResolutionLabel();
            }
        });

        // Frame taşındığında updateDisplayInfo() metodunu çağır
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if (isShowing()) { // Pencerenin gösterildiğinden emin ol
                    updateDisplayInfo();
                }
            }
        });


        // OperatingSystemMXBean örneğini alalım
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Her 1 saniyede bir güncellemek için Timer
        timer = new Timer(1000, e -> updateStats());
        timer.start();
    }

    private void updateResolutionLabel() {

        Dimension jpanelResolution = getSize(); // JPanel resolution

        Window window = SwingUtilities.getWindowAncestor(this); // JFrame resolution
        if (window != null) {
            int jframeWidth = window.getWidth();
            int jframeHeight = window.getHeight();
            // panel: %d x %d, window %d x %d

            resolutionLabel.setText(String.format(bundle.getString("system.info.resolutions"),
                    jpanelResolution.width, jpanelResolution.height,
                    jframeWidth, jframeHeight));




        }

        updateDisplayInfo();



    }

    private void updateDisplayInfo() {

        // Pencerenin konumunu al

        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        int monitorCount = screens.length;

        // Şu anki ekranı bul
        GraphicsDevice currentScreen = ge.getDefaultScreenDevice();
        String currentScreenId = null;// = currentScreen.getIDstring();

        if (isShowing()) {

            Point windowLocation = getLocationOnScreen();
            // Pencerenin bulunduğu ekranı belirle
            for (GraphicsDevice screen : screens) {
                Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
                if (screenBounds.contains(windowLocation)) {
                    // Bulunan ekranın ID'sini al
                    currentScreenId = screen.getIDstring();
                    // Burada ekran bilgisini güncelleyebilirsiniz. Örnek:
                    System.out.println("Pencere şu anda ekran: " + currentScreenId);
                    // İsteğe bağlı: Bir etiket veya başka bir bileşende de gösterebilirsiniz
                    // myScreenLabel.setText("Ekran: " + currentScreenId);
                    break;
                }
            }
        }

        // Monitör bilgilerini topla
        StringBuilder monitorsInfo = new StringBuilder();
        for (int i = 0; i < screens.length; i++) {
            DisplayMode dm = screens[i].getDisplayMode();

            //String formattedTxt = (i + 1) + " " + dm.getWidth() + " " + dm.getHeight();
            String formattedTxt = String.format(bundle.getString("system.info.display.format"),
                                        (i + 1), dm.getWidth(), dm.getHeight());
            monitorsInfo.append(formattedTxt);
            if (i < screens.length - 1) {
                monitorsInfo.append("\n");
            }
        }



        // String.format ile mesajı oluştur

        String formattedText = String.format(bundle.getString("system.info.displays"),
                monitorCount, currentScreenId, monitorsInfo.toString());


        //String formattedText = monitorCount + " "  + currentScreenId + monitorsInfo.toString();
        // JLabel güncelle
        displayInfoLabel.setText(formattedText);
    }


    private void updateStats() {
        // JVM bellek bilgileri
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long usedMB = usedMemory / (1024 * 1024);
        long totalMB = totalMemory / (1024 * 1024);
        memoryLabel.setText(bundle.getString("system.info.memory") + ": " + usedMB + " MB / " + totalMB + " MB");

        // İşletim sistemi toplam bellek bilgisi
        long osTotalMemory = osBean.getTotalPhysicalMemorySize() / (1024 * 1024);
        long osFreeMemory = osBean.getFreePhysicalMemorySize() / (1024 * 1024);
        long osUsedMemory = osTotalMemory - osFreeMemory;
        systemMemoryLabel.setText(bundle.getString("system.info.system.memory") + ": " + osUsedMemory + " MB / " + osTotalMemory + " MB");

        // CPU kullanım bilgisini güncelleyelim
        double cpuLoad = osBean.getProcessCpuLoad();
        if (cpuLoad < 0) {
            cpuLabel.setText(bundle.getString("system.info.cpu.usage") + ": N/A");
        } else {
            int cpuPercentage = (int) (cpuLoad * 100);
            cpuLabel.setText(bundle.getString("system.info.cpu.usage") + ": " + cpuPercentage + "%");
        }
    }

    private String getDefaultSoundOutput() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        if (mixers.length > 0) {
            return mixers[0].getName();
        }
        return bundle.getString("system.info.sound.output.unknown");
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("System Info");
            frame.setPreferredSize(new Dimension(400, 300));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SystemInfoPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
    }

    @Override
    public String getTabName() {
        return "plugin.test.system.info.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.INFO;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
