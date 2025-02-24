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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class CanvasPanel extends JPanel implements PanelPlugin{

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final String FILE_NAME = "notes.png";

    private JButton createNotesButton;
    private JScrollPane scrollPane;
    private DrawingArea drawingArea;
    private JToggleButton pencilEraserToggle;
    private JButton saveButton;
    private JCheckBox autoSaveCheckBox;

    private Timer autoSaveTimer;
    private Instant lastChangeTime;  // Son değişikliğin zamanı
    private boolean isDirty = false; // Çizimde değişiklik yapıldı mı?

    public CanvasPanel() {
        setLayout(new BorderLayout());

        File notesFile = new File(FILE_NAME);
        if (!notesFile.exists()) {
            // notes.png yoksa sadece "Create notes.png" butonunu göster
            createNotesButton = new JButton("Create notes.png");
            createNotesButton.addActionListener(e -> {
                remove(createNotesButton);
                initDrawingUI(); // Çizim arayüzünü başlat
                revalidate();
                repaint();
            });
            add(createNotesButton, BorderLayout.CENTER);
        } else {
            // notes.png varsa direkt çizim arayüzünü aç
            initDrawingUI();
        }
    }

    private void initDrawingUI() {
        // Çizim alanı (640x480) ve scrollPane
        drawingArea = new DrawingArea(WIDTH, HEIGHT);
        scrollPane = new JScrollPane(drawingArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Pencil/Eraser toggle
        pencilEraserToggle = new JToggleButton("Eraser (Kapalı)");
        pencilEraserToggle.addItemListener(e -> {
            if (pencilEraserToggle.isSelected()) {
                pencilEraserToggle.setText("Eraser (Açık)");
                drawingArea.setEraserMode(true);
            } else {
                pencilEraserToggle.setText("Eraser (Kapalı)");
                drawingArea.setEraserMode(false);
            }
        });

        // Save butonu
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveImage());

        // AutoSave checkbox
        autoSaveCheckBox = new JCheckBox("AutoSave", true);

        // Alt panel (toggle + save + autosave)
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(pencilEraserToggle);
        bottomPanel.add(saveButton);
        bottomPanel.add(autoSaveCheckBox);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Mause hareketlerinde vs. son değişiklik zamanını güncellemek için
        drawingArea.addChangeListener(() -> {
            lastChangeTime = Instant.now();
            isDirty = true;
        });

        // AutoSave Timer’ı
        autoSaveTimer = new Timer(1000, e -> checkAutoSave());
        autoSaveTimer.start();

        // Eğer mevcut bir notes.png varsa, onu yükleyelim
        File notesFile = new File(FILE_NAME);
        if (notesFile.exists()) {
            try {
                BufferedImage loaded = ImageIO.read(notesFile);
                drawingArea.loadImage(loaded);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Clear butonu tanımı
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure?",
                    "Clear Canvas",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                drawingArea.clear(); // Canvas'ı temizle
            }
        });

// Alt panele ekleme
        bottomPanel.add(clearButton);


        // İlk değer
        lastChangeTime = Instant.now();
    }

    /**
     * 10 saniye boyunca çizimde değişiklik yoksa ve AutoSave seçiliyse kaydet.
     */
    private void checkAutoSave() {
        if (autoSaveCheckBox.isSelected() && isDirty) {
            // Şu anki zaman ile son değişiklik zamanı arasındaki fark 10 saniyeden fazla mı?
            if (Instant.now().toEpochMilli() - lastChangeTime.toEpochMilli() >= 10_000) {
                saveImage();
            }
        }
    }

    private void saveImage() {
        try {
            BufferedImage image = drawingArea.getImage();
            ImageIO.write(image, "png", new File(FILE_NAME));
            System.out.println("notes.png kaydedildi");
            isDirty = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test amaçlı main metodu: Bir çerçeve içinde CanvasPanel gösterir.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CanvasPanel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CanvasPanel());
            frame.setSize(800, 600); // Pencere boyutu
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.paint.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    /**
     * Çizim alanını temsil eden dahili sınıf.
     */
    private static class DrawingArea extends JComponent {
        private BufferedImage canvas;
        private Graphics2D g2d;
        private int width;
        private int height;

        private boolean eraserMode = false;

        private int lastX, lastY;
        private boolean mousePressed = false;

        // Değişiklikleri bildirmek için basit bir arayüz.
        private ChangeListener changeListener;

        public DrawingArea(int width, int height) {
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));

            // Yeni boş bir resim oluştur (beyaz arka plan)
            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = canvas.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();

            // Yeniden çizim yapmak için g2d’yi tekrar al
            g2d = canvas.createGraphics();
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.setColor(Color.BLACK);

            // Mouse event’leri
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastX = e.getX();
                    lastY = e.getY();
                    mousePressed = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed = false;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (mousePressed) {
                        int x = e.getX();
                        int y = e.getY();

                        if (eraserMode) {
                            // Eraser: beyaz renk, 3 piksel çapında
                            g2d.setColor(Color.WHITE);
                            g2d.setStroke(new BasicStroke(3.0f,
                                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        } else {
                            // Pencil: siyah renk, 1 piksel
                            g2d.setColor(Color.BLACK);
                            g2d.setStroke(new BasicStroke(1.0f,
                                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        }

                        g2d.drawLine(lastX, lastY, x, y);
                        repaint();

                        lastX = x;
                        lastY = y;

                        fireChange(); // Değişiklik bildir
                    }
                }
            });
        }

        public void setEraserMode(boolean eraser) {
            this.eraserMode = eraser;
        }

        public void loadImage(BufferedImage loadedImage) {
            if (loadedImage != null) {
                // Gelen resmi canvas boyutuna uyarlayabilir veya direkt aktarabilirsiniz.
                g2d.drawImage(loadedImage, 0, 0, null);
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(canvas, 0, 0, null);
        }

        public BufferedImage getImage() {
            return canvas;
        }

        public void addChangeListener(ChangeListener listener) {
            this.changeListener = listener;
        }

        private void fireChange() {
            if (changeListener != null) {
                changeListener.onChange();
            }
        }

        public void clear() {
            // Tüm çizimi beyazla doldur
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Kalem rengini yeniden siyaha döndür
            g2d.setColor(Color.BLACK);
            repaint();

            // Değişikliği bildir (otomatik kaydet vs. tetiklenmesi için)
            fireChange();
        }



    }

    /**
     * Basit bir "değişiklik" dinleyici arayüzü.
     */
    private interface ChangeListener {
        void onChange();
    }





}

