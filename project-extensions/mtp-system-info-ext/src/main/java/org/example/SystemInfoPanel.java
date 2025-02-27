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

import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;


import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

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
    private Timer timer;

    // İşletim sistemi CPU kullanım bilgilerini almak için:
    private OperatingSystemMXBean osBean;

    private final ConfigManager props = ConfigManager.getInstance();

    private final LanguageManager bundle = LanguageManager.getInstance();

    public SystemInfoPanel() {


        setLayout(new GridLayout(8, 1, 5, 5));

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

        // Label'ları panele ekleyelim
        add(javaVersionLabel);
        add(osLabel);
        add(archLabel);
        add(memoryLabel);
        add(systemMemoryLabel);
        add(cpuLabel);
        add(processorLabel);
        add(soundOutputLabel);

        // OperatingSystemMXBean örneğini alalım
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Her 1 saniyede bir güncellemek için Timer
        timer = new Timer(1000, e -> updateStats());
        timer.start();
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
}
