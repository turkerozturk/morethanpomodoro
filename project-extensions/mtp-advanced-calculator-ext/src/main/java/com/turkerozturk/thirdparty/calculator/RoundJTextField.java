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
package com.turkerozturk.thirdparty.calculator;
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
import javax.swing.JTextField;
import java.awt.*;

public class RoundJTextField extends JTextField {
    private static final long serialVersionUID = 1L;
    private Shape shape;

    public RoundJTextField(int size) {
        super(size);
        setOpaque(false); // As we will paint the background
        setBorder(null);  // Remove the border outline
        setColumns(10);
        setBackground(new Color(213, 178, 224));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15); // Making round corners
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Do nothing to avoid drawing any border
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
        }
        return shape.contains(x, y);
    }
}
