package com.turkerozturk.jpanels;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Rendering kalitesini artır
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Renk geçiş durakları (0.0 = başlangıç, 1.0 = bitiş)
        float[] fractions = {0.1f, 0.2f, 0.3f, 0.8f, 0.9f, 1.0f}; // İlk renk %70, ikinci renk %30

        // Renkleri alfa (şeffaflık) ile ayarla
        Color opaqueColor = new Color(0, 0, 0, 255);  // Siyah, tamamen opak
        Color transparentColor = new Color(0, 0, 0, 0);      // Siyah, tamamen şeffaf

        // Renkler (ilk %70 mavi, son %30 beyaz)
        Color[] colors = {Color.BLACK,
                transparentColor,
                Color.WHITE,
                Color.WHITE,
                transparentColor,
                Color.BLACK};

        // Gradient oluştur (Yukarıdan aşağı)
        LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, getHeight(), fractions, colors);

        // Gradient ile doldur
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Gradient Panel");
        GradientPanel panel = new GradientPanel();

        frame.add(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
