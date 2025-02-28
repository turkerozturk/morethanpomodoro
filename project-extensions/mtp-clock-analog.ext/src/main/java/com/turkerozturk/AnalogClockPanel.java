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


import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.ExtensionCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class AnalogClockPanel extends JPanel implements PanelPlugin{

    // Config properties keys
    private static final String KEY_BACKGROUND = "analog.clock.background";
    private static final String KEY_CIRCLE = "analog.clock.circle";
    private static final String KEY_HOUR_HAND = "analog.clock.hourHand";
    private static final String KEY_MINUTE_HAND = "analog.clock.minuteHand";
    private static final String KEY_SECOND_HAND = "analog.clock.secondHand";
    private static final String KEY_NUMBERS = "analog.clock.numbers";

    // Colors
    private Color backgroundColor;
    private Color circleColor;
    private Color hourHandColor;
    private Color minuteHandColor;
    private Color secondHandColor;
    private Color numbersColor;

    // Properties loaded from config.properties
    ConfigManager props = ConfigManager.getInstance();

    public AnalogClockPanel() {
        loadConfig();
        // Timer: her 50 ms'de bir güncelleme (akıcı animasyon için)
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }

    /**
     * config.properties dosyasını yükler ve renkleri ayarlar.
     */
    private void loadConfig() {


        // Renkleri yükle, dosyada yoksa varsayılan değerleri kullan.
        backgroundColor = getColor(KEY_BACKGROUND, Color.WHITE);
        circleColor     = getColor(KEY_CIRCLE, Color.BLACK);
        hourHandColor   = getColor(KEY_HOUR_HAND, Color.BLACK);
        minuteHandColor = getColor(KEY_MINUTE_HAND, Color.BLACK);
        secondHandColor = getColor(KEY_SECOND_HAND, Color.RED);
        numbersColor    = getColor(KEY_NUMBERS, Color.BLACK);
    }

    /**
     * Belirtilen property anahtarına göre rengi döndürür.
     * Eğer bulunamazsa defaultColor değerini döndürür.
     * Renk değeri "#RRGGBB" formatında olmalıdır.
     */
    private Color getColor(String key, Color defaultColor) {
        String colorStr = props.getProperty(key);
        if (colorStr != null) {
            try {
                return Color.decode(colorStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("Geçersiz renk değeri " + colorStr + " for key: " + key);
            }
        }
        return defaultColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Grafik ayarları
        Graphics2D g2 = (Graphics2D) g.create();
        // Anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Panelin tamamını dolduran arka plan
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Saatin merkezini ve yarıçapını hesapla.
        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h);
        int margin = size / 20;
        int radius = (size / 2) - margin;
        int centerX = w / 2;
        int centerY = h / 2;

        // Çemberi çiz (saatin dış sınırı)
        g2.setColor(circleColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Saat rakamlarını çiz (1'den 12'ye)
        g2.setColor(numbersColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, radius / 8));
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(30 * i - 90); // -90: 12 o'clock
            // Rakamın konumunu çemberin çevresi üzerinde belirle (biraz içeri doğru)
            int numRadius = radius - margin;
            int x = centerX + (int) (numRadius * Math.cos(angle));
            int y = centerY + (int) (numRadius * Math.sin(angle));
            // Rakamın genişliğini ve yüksekliğini alarak konumlamayı ortala
            String numStr = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(numStr);
            int strHeight = fm.getAscent();
            g2.drawString(numStr, x - strWidth / 2, y + strHeight / 2);
        }

        // Zamanı al
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int milli = now.getNano() / 1_000_000;

        // Açılar hesapla (radyan cinsinden)
        double secondAngle = Math.toRadians((second + milli / 1000.0) * 6 - 90); // saniye: 6 derece her saniye
        double minuteAngle = Math.toRadians((minute + second / 60.0) * 6 - 90);
        double hourAngle   = Math.toRadians(((hour % 12) + minute / 60.0) * 30 - 90);

        // Akrep (saat ibresi) uzunluğu: %50-60
        int hourLength = (int) (radius * 0.5);
        // Yelkovan (dakika ibresi) uzunluğu: %70
        int minuteLength = (int) (radius * 0.7);
        // Saniye ibresi uzunluğu: %80
        int secondLength = (int) (radius * 0.8);

        // Akrep çizimi
        g2.setColor(hourHandColor);
        g2.setStroke(new BasicStroke(6));
        int hourX = centerX + (int) (hourLength * Math.cos(hourAngle));
        int hourY = centerY + (int) (hourLength * Math.sin(hourAngle));
        g2.drawLine(centerX, centerY, hourX, hourY);

        // Yelkovan çizimi
        g2.setColor(minuteHandColor);
        g2.setStroke(new BasicStroke(4));
        int minuteX = centerX + (int) (minuteLength * Math.cos(minuteAngle));
        int minuteY = centerY + (int) (minuteLength * Math.sin(minuteAngle));
        g2.drawLine(centerX, centerY, minuteX, minuteY);

        // Saniye çizgisi
        g2.setColor(secondHandColor);
        g2.setStroke(new BasicStroke(2));
        int secondX = centerX + (int) (secondLength * Math.cos(secondAngle));
        int secondY = centerY + (int) (secondLength * Math.sin(secondAngle));
        g2.drawLine(centerX, centerY, secondX, secondY);

        // Orta noktada dolgu (merkez)
        g2.setColor(circleColor);
        g2.fillOval(centerX - 5, centerY - 5, 10, 10);

        g2.dispose();
    }

    // Test için standalone JFrame içerisinde AnalogClock panelini çalıştır.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Analog Clock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            AnalogClockPanel clock = new AnalogClockPanel();
            frame.add(clock);
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.clock.analog.title";
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
