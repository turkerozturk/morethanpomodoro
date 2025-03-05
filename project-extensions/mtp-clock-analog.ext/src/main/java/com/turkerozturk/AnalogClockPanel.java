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
import java.awt.geom.*;
import java.time.LocalTime;

public class AnalogClockPanel extends JPanel implements PanelPlugin {

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

        // Her 50 ms’de bir repaini tetikleyerek canlı saat efekti
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
        backgroundColor = getColor(KEY_BACKGROUND, new Color(0,0,0,0)); // default transparent
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
            if(!colorStr.trim().isEmpty()) {
                try {
                    return Color.decode(colorStr.trim());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid color value " + colorStr + " for config key: " + key);
                }
            }
        }
        return defaultColor;
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- 1) SVG koordinatlarını ölçeklemek için transform ayarla ---
        // SVG boyutu: 400×400
        float scaleFactor = Math.min(w, h) / 400f;
        // SVG, orijinde değil ortalanmış olsun diye panelin ortasına çeviriyoruz
        // (opsiyonel: tam sol üst köşeden de başlayabilirsin)
        g2.translate((w - 400 * scaleFactor) / 2.0, (h - 400 * scaleFactor) / 2.0);
        g2.scale(scaleFactor, scaleFactor);

        // --- 2) SVG’den gelen çizimleri “elle” Java2D ile yap ---

        // 2.1 Arka plan dikdörtgeni (#f0f0f0)
        g2.setColor(backgroundColor); // 0xf0f0f0
        g2.fillRect(0, 0, 400, 400);

        // 2.2 Saatin dış çerçevesi (radial gradient)
        //    center = (200,200), radius = 180
        //    renk geçişi: #cccccc -> #f2f2f2 -> #999999
        {
            Point2D center = new Point2D.Float(200, 200);
            float radius = 180f;
            float[] dist = {0f, 0.4f, 1f};
            Color[] colors = {
                    new Color(0xcccccc),
                    new Color(0xf2f2f2),
                    new Color(0x999999)
            };
            RadialGradientPaint outerRingPaint =
                    new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(outerRingPaint);

            Ellipse2D outerCircle = new Ellipse2D.Float(
                    200 - radius, 200 - radius, radius * 2, radius * 2);
            g2.fill(outerCircle);

            // Kenar çizgisi
            g2.setColor(circleColor); // 0x555555
            g2.setStroke(new BasicStroke(2f));
            g2.draw(outerCircle);
        }

        // 2.3 Saat yüzeyi (beyaz -> gri radial gradient)
        {
            Point2D center = new Point2D.Float(200, 200);
            float radius = 160f;
            float[] dist = {0f, 0.9f};
            Color[] colors = {
                    new Color(0xffffff),
                    new Color(0xdddddd)
            };
            RadialGradientPaint facePaint =
                    new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(facePaint);

            Ellipse2D faceCircle = new Ellipse2D.Float(
                    200 - radius, 200 - radius, radius * 2, radius * 2);
            g2.fill(faceCircle);

            g2.setColor(new Color(0xdddddd));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(faceCircle);
        }

        // 2.4 Dakika ve saat çizgileri
        //     minuteMarks: (x1=200,y1=40) -> (200,50), stroke=#666666, width=2
        //     hourMarks:   (x1=200,y1=30) -> (200,55), stroke=#333333, width=4
        //     Her 5 dakikada bir hourMark, diğerinde minuteMark
        for (int i = 0; i < 60; i++) {
            AffineTransform old = g2.getTransform();
            double angle = Math.toRadians(i * 6); // i=0..59, 6 derece aralıklı
            g2.rotate(angle, 200, 200);

            if (i % 5 == 0) {
                // hour mark
                g2.setColor(new Color(0x333333));
                g2.setStroke(new BasicStroke(4f));
                g2.drawLine(200, 30, 200, 55);
            } else {
                // minute mark
                g2.setColor(new Color(0x666666));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(200, 40, 200, 50);
            }
            g2.setTransform(old);
        }

        // 2.5 Saat rakamları (SVG’deki gibi konumlar ve döndürmeler)
        g2.setColor(numbersColor); // 0x333333
        g2.setFont(new Font("Arial", Font.PLAIN, 24));

        // Düz yazılan 12, 3, 6, 9
        drawCenteredString(g2, "12", 200, 78);
        drawCenteredString(g2, "6",  200, 322);
        drawCenteredString(g2, "3",  322, 206);
        drawCenteredString(g2, "9",   78, 206);

        // Ara rakamlar (1,2,4,5,7,8,10,11) – her biri yerleştirilip ayrıca döndürülmüş
        //  Örnek: <text x="270" y="105" transform="rotate(30,270,105)">1</text>
        //  Java2D’de aynı etkiyi elde etmek için:
        drawRotatedString(g2, "1",  270, 105,  30);
        drawRotatedString(g2, "2",  298, 136,  60);
        drawRotatedString(g2, "4",  298, 264, -60);
        drawRotatedString(g2, "5",  270, 295, -30);
        drawRotatedString(g2, "7",  130, 295,  30);
        drawRotatedString(g2, "8",  102, 264,  60);
        drawRotatedString(g2, "10", 102, 136, -60);
        drawRotatedString(g2, "11", 130, 105, -30);

        // 2.6 Akrep & Yelkovan & Saniye (Dinamik çizim)
        //     – Burada SVG’deki sabit path yerine, LocalTime’dan açı hesaplayarak çiziyoruz.
        LocalTime now = LocalTime.now();
        int hour = now.getHour() % 12;
        int minute = now.getMinute();
        int second = now.getSecond();
        int milli = now.getNano() / 1_000_000;

        double secondAngle = Math.toRadians((second + milli / 1000.0) * 6 - 90);
        double minuteAngle = Math.toRadians((minute + second / 60.0) * 6 - 90);
        double hourAngle   = Math.toRadians((hour + minute / 60.0) * 30 - 90);

        // Uzunluklar (SVG’deki boyutlara uydurmak istersen orantısal verelim)
        double hourLength   = 60;  // ~ %50 of radius=120 / senin isteğine göre
        double minuteLength = 90;  // ~ %70 of radius=120
        double secondLength = 100; // ~ %80 of radius=120

        // Akrep
        g2.setStroke(new BasicStroke(6f));
        g2.setColor(hourHandColor);
        drawHand(g2, 200, 200, hourAngle, hourLength);

        // Yelkovan
        g2.setStroke(new BasicStroke(4f));
        g2.setColor(minuteHandColor);
        drawHand(g2, 200, 200, minuteAngle, minuteLength);

        // Saniye (istersen kaldır)
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(secondHandColor);
        drawHand(g2, 200, 200, secondAngle, secondLength);

        // 2.7 Saatin ortasındaki küçük dairesel merkez
        g2.setColor(new Color(0x666666));
        g2.fillOval(200 - 5, 200 - 5, 10, 10);
        g2.setColor(new Color(0x333333));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(200 - 5, 200 - 5, 10, 10);

        // 2.8 Gölge efekti: <ellipse cx="200" cy="400" rx="150" ry="20" fill="rgba(0,0,0,0.2)" transform="translate(0, -20)" />
        //     => Java2D’de: ellipse merkez(200,380), rx=150, ry=20
        {
            Ellipse2D shadow = new Ellipse2D.Float(
                    200 - 150,   // x = 50
                    380 - 20,    // y = 360
                    300,         // width = 2×rx
                    40           // height= 2×ry
            );
            g2.setColor(new Color(0, 0, 0, 50)); // 50 ~ %20 opaklık
            g2.fill(shadow);
        }

        // Transform ve g2 serbest
        g2.dispose();
    }

    // Yardımcı fonksiyonlar

    /**
     * Belirtilen açıda (radyan cinsinden) merkezden başlayarak bir çizgi çizer.
     */
    private void drawHand(Graphics2D g2, int cx, int cy, double angle, double length) {
        int x2 = cx + (int) (length * Math.cos(angle));
        int y2 = cy + (int) (length * Math.sin(angle));
        g2.drawLine(cx, cy, x2, y2);
    }

    /**
     * Metni (x,y) koordinatının ortasına gelecek şekilde çizer.
     * Java’da (x, y) text'in alt sol köşesi varsayıldığı için ufak bir hesaplama yapıyoruz.
     */
    private void drawCenteredString(Graphics2D g2, String text, float x, float y) {
        FontMetrics fm = g2.getFontMetrics();
        float textWidth = fm.stringWidth(text);
        float textHeight = fm.getAscent(); // ya da getAscent() + getDescent() / 2
        g2.drawString(text, x - textWidth / 2, y + textHeight / 2);
    }

    /**
     * Belirtilen noktada (x,y), belirtilen açı (degree cinsinden) kadar döndürülmüş şekilde
     * metni ortalayarak çizer.
     */
    private void drawRotatedString(Graphics2D g2, String text, float x, float y, double degrees) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(degrees));
        drawCenteredString(g2, text, 0, 0);
        g2.setTransform(old);
    }

    // Basit test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SVG Clock Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 450);

            AnalogClockPanel panel = new AnalogClockPanel();
            frame.add(panel);

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
