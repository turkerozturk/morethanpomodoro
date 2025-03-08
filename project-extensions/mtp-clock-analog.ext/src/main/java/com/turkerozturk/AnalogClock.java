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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class AnalogClock extends JPanel {

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


    private JButton toggleButton;
    private JLabel clockLabel;

    // Panelin orijinal olarak bulunduğu yer (ana frame içindeki parent)
    private Container originalParent;
    // Bu paneli sonradan taşıyacağımız pencere (ayrı frame)
    private JFrame floatFrame;

    private boolean isMatrixEffectEnabled = true;
    private MatrixEffectVariation matrixEffectVariation = MatrixEffectVariation.VARIATION_1_AND_2;

    private boolean isBottomShadowEnabled = true;

    // Properties loaded from config.properties
    ConfigManager props = ConfigManager.getInstance();

    public AnalogClock() {
        loadConfig();

        // Her 50 ms’de bir repaini tetikleyerek canlı saat efekti
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /*
                // basla sadece matrix variation2 için
                if (matrixEffectEnabled) {
                    // Eğer henüz sütunlar oluşturulmadıysa (panel boyutu 0x0 iken) init et
                    if (columns.isEmpty() && getWidth() > 0 && getHeight() > 0) {
                        initColumns(getWidth(), getHeight());
                    }
                    updateColumns();
                }
                // bitti sadece matrix variation2 için
                */

                repaint();
            }





        });
        timer.start();

        // basla sadece matrix variation2 için
        // Panelin constructor'ında (veya uygun bir yerde) ekleyebilirsiniz:
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Panelin yeni genişlik/yüksekliği belli olduğunda
                // columns listesini sıfırlayıp yeniden oluşturabilirsiniz:
                columns.clear();
                initColumns(getWidth(), getHeight());
            }
        });
        // bitti sadece matrix variation2 için



        // Paneli yeni pencereye taşıma butonu
        toggleButton = new JButton("Ayrı Pencereye Al");
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //toggleButton.addActionListener(e -> popOutPanel());
        //add(toggleButton);



        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Çift tıklama kontrolü
                   // System.out.println("hello");
                    popOutPanel();
                }
            }
        });



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

        int variationValue = Integer.parseInt(props.getProperty("analog.clock.matrix.effect.variation", "0"));
        matrixEffectVariation = MatrixEffectVariation.fromInt(variationValue);

        isMatrixEffectEnabled = Integer.parseInt(props.getProperty("analog.clock.is.matrix.effect.enabled")) == 1;
        isBottomShadowEnabled = Integer.parseInt(props.getProperty("analog.clock.is.bottom.shadow.enabled")) == 1;


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
        Graphics2D g2dForAnalogClock = (Graphics2D) g.create();
        g2dForAnalogClock.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // --- 1) SVG koordinatlarını ölçeklemek için transform ayarla ---
        // SVG boyutu: 400×400
        float scaleFactor = Math.min(w, h) / 400f;
        // SVG, orijinde değil ortalanmış olsun diye panelin ortasına çeviriyoruz
        // (opsiyonel: tam sol üst köşeden de başlayabilirsin)
        g2dForAnalogClock.translate((w - 400 * scaleFactor) / 2.0, (h - 400 * scaleFactor) / 2.0);
        g2dForAnalogClock.scale(scaleFactor, scaleFactor);

        if (isBottomShadowEnabled) {
            drawBottomShadow(g2dForAnalogClock);
        }

        // --- 2) SVG’den gelen çizimleri “elle” Java2D ile yap ---

        // 2.1 Arka plan dikdörtgeni (#f0f0f0)
        g2dForAnalogClock.setColor(backgroundColor); // 0xf0f0f0
        g2dForAnalogClock.fillRect(0, 0, 400, 400);

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
            g2dForAnalogClock.setPaint(outerRingPaint);

            Ellipse2D outerCircle = new Ellipse2D.Float(
                    200 - radius, 200 - radius, radius * 2, radius * 2);
            g2dForAnalogClock.fill(outerCircle);

            // Kenar çizgisi
            g2dForAnalogClock.setColor(circleColor); // 0x555555
            g2dForAnalogClock.setStroke(new BasicStroke(2f));
            g2dForAnalogClock.draw(outerCircle);
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
            g2dForAnalogClock.setPaint(facePaint);

            Ellipse2D faceCircle = new Ellipse2D.Float(
                    200 - radius, 200 - radius, radius * 2, radius * 2);
            g2dForAnalogClock.fill(faceCircle);

            g2dForAnalogClock.setColor(new Color(0xdddddd));
            g2dForAnalogClock.setStroke(new BasicStroke(2f));
            g2dForAnalogClock.draw(faceCircle);
        }

        // 2.4 Dakika ve saat çizgileri
        //     minuteMarks: (x1=200,y1=40) -> (200,50), stroke=#666666, width=2
        //     hourMarks:   (x1=200,y1=30) -> (200,55), stroke=#333333, width=4
        //     Her 5 dakikada bir hourMark, diğerinde minuteMark
        for (int i = 0; i < 60; i++) {
            AffineTransform old = g2dForAnalogClock.getTransform();
            double angle = Math.toRadians(i * 6); // i=0..59, 6 derece aralıklı
            g2dForAnalogClock.rotate(angle, 200, 200);

            if (i % 5 == 0) {
                // hour mark
                g2dForAnalogClock.setColor(new Color(0x333333));
                g2dForAnalogClock.setStroke(new BasicStroke(4f));
                g2dForAnalogClock.drawLine(200, 30, 200, 55);
            } else {
                // minute mark
                g2dForAnalogClock.setColor(new Color(0x666666));
                g2dForAnalogClock.setStroke(new BasicStroke(2f));
                g2dForAnalogClock.drawLine(200, 40, 200, 50);
            }
            g2dForAnalogClock.setTransform(old);
        }

        // 2.5 Saat rakamları (SVG’deki gibi konumlar ve döndürmeler)
        g2dForAnalogClock.setColor(numbersColor); // 0x333333
        g2dForAnalogClock.setFont(new Font("Arial", Font.PLAIN, 24));

        // Düz yazılan 12, 3, 6, 9
        drawCenteredString(g2dForAnalogClock, "12", 200, 72);
        drawCenteredString(g2dForAnalogClock, "6",  200, 323);
        drawCenteredString(g2dForAnalogClock, "3",  330, 198);
        drawCenteredString(g2dForAnalogClock, "9",   71, 198);

        // Ara rakamlar (1,2,4,5,7,8,10,11) – her biri yerleştirilip ayrıca döndürülmüş
        //  Örnek: <text x="270" y="105" transform="rotate(30,270,105)">1</text>
        //  Java2D’de aynı etkiyi elde etmek için:
        drawRotatedString(g2dForAnalogClock, "1",  264, 88,  30);
        drawRotatedString(g2dForAnalogClock, "2",  311, 136,  60);
        drawRotatedString(g2dForAnalogClock, "4",  308, 263, -60);
        drawRotatedString(g2dForAnalogClock, "5",  263, 306, -30);
        drawRotatedString(g2dForAnalogClock, "7",  139, 307,  30);
        drawRotatedString(g2dForAnalogClock, "8",  94, 262,  60);
        drawRotatedString(g2dForAnalogClock, "10", 88, 137, -60);
        drawRotatedString(g2dForAnalogClock, "11", 136, 89, -30);

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
        g2dForAnalogClock.setStroke(new BasicStroke(6f));
        g2dForAnalogClock.setColor(hourHandColor);
        drawHand(g2dForAnalogClock, 200, 200, hourAngle, hourLength);

        // Yelkovan
        g2dForAnalogClock.setStroke(new BasicStroke(4f));
        g2dForAnalogClock.setColor(minuteHandColor);
        drawHand(g2dForAnalogClock, 200, 200, minuteAngle, minuteLength);

        // Saniye (istersen kaldır)
        g2dForAnalogClock.setStroke(new BasicStroke(2f));
        g2dForAnalogClock.setColor(secondHandColor);
        drawHand(g2dForAnalogClock, 200, 200, secondAngle, secondLength);

        // 2.7 Saatin ortasındaki küçük dairesel merkez
        g2dForAnalogClock.setColor(new Color(0x666666));
        g2dForAnalogClock.fillOval(200 - 5, 200 - 5, 10, 10);
        g2dForAnalogClock.setColor(new Color(0x333333));
        g2dForAnalogClock.setStroke(new BasicStroke(2f));
        g2dForAnalogClock.drawOval(200 - 5, 200 - 5, 10, 10);



        // Transform ve g2dForAnalogClock serbest
        g2dForAnalogClock.dispose();

        // BASLA matrix animasyonu icin
        // SVG merkez (200,200) --> panel merkez:
        float offsetX = (w - 400 * scaleFactor) / 2.0f;
        float offsetY = (h - 400 * scaleFactor) / 2.0f;
        float centerX = offsetX + 200 * scaleFactor;
        float centerY = offsetY + 200 * scaleFactor;
        float clockRadius = 180 * scaleFactor;  // Saatin dış çerçevesi, SVG'de radius=180

        // Matrix efekti isteniyorsa...
        if (isMatrixEffectEnabled) {
            Graphics2D g2dForMatrixAnimation = (Graphics2D) g.create();

            // Panelin tamamını kaplayan dikdörtgen
            Area panelArea = new Area(new Rectangle(0, 0, w, h));

            // Saatin dış çemberinin panel koordinatları
            // (merkez (centerX, centerY), yarıçap = clockRadius)
            Ellipse2D clockArea = new Ellipse2D.Double(
                    centerX - clockRadius,
                    centerY - clockRadius,
                    clockRadius * 2,
                    clockRadius * 2
            );

            // Daireyi alanın içinden çıkart
            panelArea.subtract(new Area(clockArea));

            // Artık panelArea, saatin dışındaki bölge
            g2dForMatrixAnimation.setClip(panelArea);

            // İsterseniz önce bu dış bölgeyi boyayabilirsiniz (örn. siyah):
            g2dForMatrixAnimation.setColor(Color.BLACK);
            g2dForMatrixAnimation.fillRect(0, 0, w, h);

            // Ardından Matrix efektini çiz
            switch (matrixEffectVariation) {
                case NONE:
                    break;
                case VARIATION_1:
                    drawMatrixEffectVariation1(g2dForMatrixAnimation, w, h);
                    break;
                case VARIATION_2:
                    drawMatrixEffectVariation2(g2dForMatrixAnimation, w, h);
                    break;
                case VARIATION_1_AND_2:
                    drawMatrixEffectVariation1(g2dForMatrixAnimation, w, h);
                    drawMatrixEffectVariation2(g2dForMatrixAnimation, w, h);
                    break;
            }


            // basla sadece matrix variation2 için
            //if (matrixEffectEnabled) {
                // Eğer henüz sütunlar oluşturulmadıysa (panel boyutu 0x0 iken) init et
                if (columns.isEmpty() && w > 0 && h > 0) {
                    initColumns(w, h);
                }
                updateColumns();
            //}
            // bitti sadece matrix variation2 için
            g2dForMatrixAnimation.dispose();
        }
        // BITTI matrix animasyonu icin



    }

    private void drawBottomShadow(Graphics2D g2dForAnalogClock) {
        // 2.8 Gölge efekti: <ellipse cx="200" cy="400" rx="150" ry="20" fill="rgba(0,0,0,0.2)" transform="translate(0, -20)" />
        //     => Java2D’de: ellipse merkez(200,380), rx=150, ry=20
        {
            // Elips tanımı (gölge)
            Ellipse2D shadow = new Ellipse2D.Float(
                    200 - 150,   // x = 50
                    380 - 15,    // y = 360
                    300,         // width = 2×rx
                    25           // height= 2×ry
            );

            // Degrade merkezi ve yarıçap
            Point2D center = new Point2D.Float(200, 380);
            float radius = 150; // Elipsin yatay yarıçapı kadar

            // Degrade ayarları:
            // fraction 0 --> (merkez) siyah
            // fraction 1 --> (dış) tamamen şeffaf
            float[] dist = {0f, 0.4f, 1f};
            Color[] colors = {
                    new Color(0, 0, 0, 255),  // merkez (bir renk)
                    new Color(99, 99, 99, 150),  // merkez (bir renk)

                    new Color(0, 0, 0, 0)     // dış kısım (transparent)
            };

            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);

            // Degrade boyamasını ayarla ve elipsi doldur
            g2dForAnalogClock.setPaint(rgp);
            g2dForAnalogClock.fill(shadow);
        }
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
            JFrame frame = new JFrame("MoreThanPomodoro Clock Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(450, 450);

            AnalogClockPanel panel = new AnalogClockPanel();
            frame.add(panel);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Aşağıdaki gibi güncelleyerek, getSize() ile alınan genişlik ve yükseklik değerlerinden en küçük olanı hem genişlik hem yükseklik olarak döndürebilirsiniz:
    @Override
    public Dimension getPreferredSize() {
        int minSize = Math.min(getWidth(), getHeight());
        return new Dimension(minSize, minSize);
    }


    /**
     * Paneli bulunduğu yerden çıkarıp yeni bir frame'e ekler.
     * Yeni frame "Always on top" olacak ve modal olmayacaktır.
     */
    private void popOutPanel() {
        // Eğer zaten başka bir frame'e taşındıysa, tekrar taşımaya gerek yok
        // (bu örnekte buton disable edildiği için bir daha tıklanamaz ama
        //  yine de kontrol ekleyebilirsiniz.)
        if (floatFrame != null) {
            return;
        }

        // Orijinal parent'ı kaydet
        originalParent = getParent();
        if (originalParent == null) {
            return;  // parent yoksa işlem yapma
        }

        // Paneli mevcut parent'tan kaldır
        originalParent.remove(this);
        originalParent.revalidate();
        originalParent.repaint();

        // Yeni frame oluştur
        floatFrame = new JFrame("Analog Clock - Floating");
        floatFrame.setAlwaysOnTop(true);
        floatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Yeni pencere kapatılırken paneli geri ana frame'e taşıyalım
        floatFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Pencere kapanmadan hemen önce paneli geri taşı
                movePanelBack();
            }
        });

        // Paneli yeni frame'e ekle
        floatFrame.getContentPane().setLayout(new BorderLayout());
        floatFrame.getContentPane().add(this, BorderLayout.CENTER);

        //floatFrame.getContentPane().setSize(350,350);


        floatFrame.pack();
        floatFrame.setLocationRelativeTo(null);  // Ekran ortasına koy
        floatFrame.setVisible(true);

        // Artık butonu devre dışı bırakıyoruz
        toggleButton.setEnabled(false);
        toggleButton.setText("Ayrı Pencerede");
    }

    /**
     * Panel, yeni frame kapatılınca tekrar eski yerine (ana pencereye) dönsün.
     */
    private void movePanelBack() {
        if (floatFrame != null) {
            // Yeni frame'den paneli çıkar
            floatFrame.remove(this);
            floatFrame.dispose();
            floatFrame = null;
        }
        if (originalParent != null) {
            // Ana parent'a geri ekle
            originalParent.add(this);
            originalParent.revalidate();
            originalParent.repaint();
        }
        // Butonu tekrar aktif et ve yazısını ilk hâline döndür
        toggleButton.setEnabled(true);
        toggleButton.setText("Ayrı Pencereye Al");
    }


    /*
    // Sınıf içinde ekleyin:
    private int matrixSlowCounter = 0;
    private final int MATRIX_SLOW_FACTOR = 1; // kaç tetiklemede bir güncellesin
    */

    /**
     * BU VARYASYON SADECE TEK METODDAN OLUSUYOR.
     * Panelin tamamına (ya da istediğiniz bir bölgeye) akan
     * "Matrix" tarzı yeşil karakterler çizer.
     *
     * @param g2 Graphics2D nesnesi
     * @param w  panel genişliği
     * @param h  panel yüksekliği
     */
    private void drawMatrixEffectVariation1(Graphics2D g2, int w, int h) {
        // 1) Grafik ayarları (font, renk, vs.)
        g2.setFont(new Font("Monospaced", Font.PLAIN, 25));
        g2.setColor(new Color(0x33CC33)); // Matrix yeşili (istenirse alpha da eklenebilir)

        // 2) Bu örnekte, "kolon kolon" akan karakterler varsayalım.
        //    (Gerçek kullanımda bunları class seviyesindeki ArrayList / dizi
        //     şeklinde saklayıp Timer ile sürekli güncellersiniz.)
        //    Aşağıda her bir kolon için rastgele karakterleri "y" konumlarına çiziyormuşuz gibi düşünelim.

        // Sabit sütun aralığı
        int columnWidth = 20;
        for (int x = 0; x < w; x += columnWidth) {
            // Her kolonda rasgele karakter sayısı/konumları varmış gibi simüle edelim
            int numberOfChars = 5 + (int)(Math.random() * 10); // 5-15 arası satır
            for (int i = 0; i < numberOfChars; i++) {
                // Rastgele bir unicode karakter üret (örnek olarak 0x30A0-0x30FF arası Japonca Katakana gibi)
                char randomChar = (char) (0x30A0 + (int)(Math.random() * 96));
                // Y pozisyonu (bu çizimde rastgele, ama siz animasyonda sabit artış yapacaksınız)
                int y = (int)(Math.random() * (h));

                // Çizim
                g2.drawString(String.valueOf(randomChar), x, y);
            }
        }

        // 3) Gerçek animasyon için Timer ile "düşen" konumları güncelleyip repaint etmeniz gerekli.
        //    Bu örnek sadece her repaint'te rastgele bir görsel verir.
    }




    /**
     * BU VARYASYON AŞAĞIDAN İTİBAREN BİRKAÇ METODDAN OLUŞUYOR.
     * Bu metod, columns listesindeki sütunları dolaşarak
     * her bir karakteri (x, y) konumunda yeşil olarak çizer.
     */
    private void drawMatrixEffectVariation2(Graphics2D g2, int panelWidth, int panelHeight) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2.setColor(Color.GREEN);

        for (MatrixColumn col : columns) {
            col.draw(g2);
        }
    }

    /**
     * Basit sütun (column) sınıfı. Sütun x konumunda,
     * bir dizi y konumuna sahip karakterden oluşuyor.
     */
    private class MatrixColumn {
        private int x;
        private java.util.List<CharPos> chars = new ArrayList<>();
        private int panelHeight;

        public MatrixColumn(int x, int panelHeight) {
            this.x = x;
            this.panelHeight = panelHeight;
        }

        public void initRandomChars(int height) {
            // Sütunu yukarıdan aşağıya, CHAR_SPACING aralıklarla karakter dizelim
            // Y pozisyonları -height ile +height arasında dağılabilir,
            // böylece bazıları ilk anda ekranda, bazıları yukarıdan gelecek.
            int maxCount = height / CHAR_SPACING + 5;
            for (int i = 0; i < maxCount; i++) {
                int yPos = -rand.nextInt(height); // rastgele yukarıdan başlat
                char c = randomKatakana();
                chars.add(new CharPos(c, yPos));
            }
        }

        /**
         * Her adımda y pozisyonlarını SPEED kadar artır.
         * Ekran altına düşenler en üste yeniden, yeni karakterle dönsün.
         */
        public void scrollDown(int speed) {
            for (CharPos cp : chars) {
                cp.y += speed;
                if (cp.y > panelHeight) {
                    // En alta inen karakter en başa dönsün
                    cp.y = -CHAR_SPACING;
                    cp.c = randomKatakana();
                }
            }
        }

        public void draw(Graphics2D g2) {
            for (CharPos cp : chars) {
                g2.drawString(String.valueOf(cp.c), x, cp.y);
            }
        }

        private char randomKatakana() {
            // 0x30A0 ~ 0x30FF arasında rastgele bir karakter
            return (char) (0x30A0 + rand.nextInt(0x30FF - 0x30A0));
        }
    }

    /**
     * Sütundaki her bir karakterin konum bilgisi
     */
    private static class CharPos {
        char c;
        int y;
        public CharPos(char c, int y) {
            this.c = c;
            this.y = y;
        }
    }


    /**
     * Sütunları (MatrixColumn) oluşturur. Panelin genişliğini dikkate alarak,
     * saat çemberinin kapladığı merkez bölge hariç soldan sağa sütunlar diziyoruz.
     */
    private void initColumns(int panelWidth, int panelHeight) {
        columns.clear();

        // Sütunları 0'dan panelWidth'e kadar COLUMN_WIDTH adımlarla oluştur
        for (int x = 0; x < panelWidth; x += COLUMN_WIDTH) {
            MatrixColumn col = new MatrixColumn(x, panelHeight);
            col.initRandomChars(panelHeight);
            columns.add(col);
        }
    }

    /**
     * Her timer adımında tüm sütunları aşağı doğru kaydır ve
     * ekran dışına çıkan karakterleri yeniden en üste ekle.
     */
    private void updateColumns() {
        for (MatrixColumn col : columns) {
            col.scrollDown(SPEED);
        }
    }


    private static final int CLOCK_SIZE = 360; // Merkezdeki "saat" çemberinin çapı
    private static final int CHAR_SPACING = 18; // Karakterler arasındaki düşey mesafe
    private static final int COLUMN_WIDTH = 18; // Sütunlar arası mesafe
    private static final int SPEED = 1;         // Her adımda kaç piksel hareket etsin (yavaşlatmak için küçük tutun)
    private static final int TIMER_DELAY = 50;  // 50 ms (soru gereği değiştirmiyoruz)

    //private boolean matrixEffectEnabled = true;
    private final java.util.List<MatrixColumn> columns = new ArrayList<>();
    private final Random rand = new Random();

    private enum MatrixEffectVariation {
        NONE, VARIATION_1, VARIATION_2, VARIATION_1_AND_2;

        public static MatrixEffectVariation fromInt(int value) {
            switch (value) {
                case 1:
                    return VARIATION_1;
                case 2:
                    return VARIATION_2;
                case 3:
                    return VARIATION_1_AND_2;
                default:
                    return NONE; // Geçersiz değerler için güvenli varsayılan dönüş
            }
        }
    }



}
