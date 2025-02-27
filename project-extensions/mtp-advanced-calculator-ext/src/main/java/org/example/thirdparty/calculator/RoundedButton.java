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
package org.example.thirdparty.calculator;
/*
 * This file is copied from the Advanced Calculator Made in Java Swing
 * (https://github.com/Swif7ify/Advanced-Calculator-Made-in-Java-Swing),
 * specifically from commit:
 * https://github.com/Swif7ify/Advanced-Calculator-Made-in-Java-Swing/commit/8726fda81e55af03cbe4cf972d1f1981fa19bd37
 *
 * The original code is licensed under the MIT License:
 *
 *  --- Begin MIT License Notice ---
 *  MIT License
 *
 *  Copyright (c) 2024 Swif7ify
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *  --- End MIT License Notice ---
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class RoundedButton extends JButton {

    private static final long serialVersionUID = 1L;
    private int cornerRadius;
    private static final Map<String, ColorPair> colorMap = new HashMap<>();
    
    static {
    	Color orange = new Color(245, 155, 121);
    	Color red = new Color(241, 133, 141);
    	Color purple = new Color(133, 110, 167);
    	
    	Color black = new Color(59, 9, 24);
    	Color white = new Color(255, 255, 255);
    	
    	Color reds = new Color(240, 0, 0);
    	Color blue = new Color(0, 240, 0);
    	Color green = new Color(0, 0, 240);
    	
    	addColors(new ColorPair(orange, black), "DL", "AC", "PM", "FLR", "CEL", "IT", "INTD", "MOD", "FAC", "SQRT", "CBRT", "NMROOT", "SUM", "XY", "XYZ", "PRN", "ZRO", "PRD", "DSUM", "DNOT");
    	addColors(new ColorPair(red, white), "PLUS", "MN", "MUL", "DIV", "EQU");
    	addColors(new ColorPair(purple, white), "SVN", "EHT", "NN", "FR", "FV", "SX", "ON", "TWO", "THR", "ST", "LG2", "LGN", "LG", "A", "B", "C", "D", "a+b", "a/b", "XEY", "x+y", "x^y", "CEX", "x+c", "x^c", "ANS");
    	addColors(new ColorPair(reds, white), "ah1");
    	addColors(new ColorPair(blue, white), "ah2");
    	addColors(new ColorPair(green, white), "ah3");
    }
    private static void addColors(ColorPair colorPair, String... identifiers) {
        for (String identifier : identifiers) {
        	colorMap.put(identifier, colorPair);
        }
    }
    
    public RoundedButton(String text, int cornerRadius, String identifier) {
        setText(text);
        this.cornerRadius = cornerRadius;
        setFocusPainted(false);
        setOpaque(false); 
        setBorderPainted(false);
        setBorder(null); 
        setFont(new Font("Tahoma", Font.BOLD, 15));
        
        ColorPair colorPair = colorMap.getOrDefault(identifier, new ColorPair(Color.WHITE, Color.BLACK));
        setBackground(colorPair.backgroundColor);
        setForeground(colorPair.foregroundColor);
    }
    
    private static class ColorPair {
    	Color backgroundColor;
    	Color foregroundColor;

    	ColorPair(Color backgroundColor, Color foregroundColor) {
    		this.backgroundColor = backgroundColor;
    		this.foregroundColor = foregroundColor;
    	}
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground()); 
        }
        
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        
        Icon icon = getIcon();
        if (icon != null) {
            int iconX = (getWidth() - icon.getIconWidth()) / 2;
            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, iconX, iconY);
        }

        String text = getText();
        if (!text.isEmpty()) {
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent() - (fm.getAscent() - fm.getDescent()) / 2;
            
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2;
            
            g2.setColor(getForeground());
            g2.drawString(text, x, y);
        }

        g2.dispose();
    }
}
