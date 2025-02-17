package org.example.jpanels.systeminfo;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemInfoPanel extends JPanel {

    private JLabel javaVersionLabel;
    private JLabel osLabel;
    private JLabel archLabel;
    private JLabel memoryLabel;
    private JLabel cpuLabel;
    private Timer timer;

    // İşletim sistemi CPU kullanım bilgilerini almak için:
    private OperatingSystemMXBean osBean;

    public SystemInfoPanel() {
        setLayout(new GridLayout(5, 1, 5, 5));

        // Sistem bilgilerini alalım
        javaVersionLabel = new JLabel("Java Version: " + System.getProperty("java.version"));
        osLabel = new JLabel("Operating System: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version"));
        archLabel = new JLabel("Architecture: " + System.getProperty("os.arch"));
        memoryLabel = new JLabel("Memory: ");
        cpuLabel = new JLabel("CPU Usage: ");

        // Label'ları panele ekleyelim
        add(javaVersionLabel);
        add(osLabel);
        add(archLabel);
        add(memoryLabel);
        add(cpuLabel);

        // OperatingSystemMXBean örneğini alalım
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Her 1 saniyede bir güncellemek için Timer
        timer = new Timer(1000, e -> updateStats());
        timer.start();
    }

    private void updateStats() {
        // Bellek bilgilerini güncelleyelim
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        // MB cinsine çevirelim
        long usedMB = usedMemory / (1024 * 1024);
        long totalMB = totalMemory / (1024 * 1024);
        memoryLabel.setText("Memory: " + usedMB + " MB / " + totalMB + " MB");

        // CPU kullanım bilgisini güncelleyelim
        // getProcessCpuLoad() değeri 0.0 ile 1.0 arasında döner.
        double cpuLoad = osBean.getProcessCpuLoad();
        // Eğer bilgi alınamıyorsa -1 dönebilir, buna dikkat edelim.
        if (cpuLoad < 0) {
            cpuLabel.setText("CPU Usage: N/A");
        } else {
            int cpuPercentage = (int) (cpuLoad * 100);
            cpuLabel.setText("CPU Usage: " + cpuPercentage + "%");
        }
    }

    // Panelin durdurulması gereken durumlarda timer'ı durdurmak için metod eklenebilir.
    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    // Test için standalone JFrame oluşturabilirsiniz.
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
}
