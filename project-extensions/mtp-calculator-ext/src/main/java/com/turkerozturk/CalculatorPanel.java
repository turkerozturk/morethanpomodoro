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

import com.turkerozturk.initial.ExtensionCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CalculatorPanel extends JPanel implements ActionListener, KeyListener, PanelPlugin {

    private JTextField display;
    private String currentInput = "";
    private double result = 0;
    private String lastOperator = "=";

    public CalculatorPanel() {
        setLayout(new BorderLayout());
        // Ekran
        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(display, BorderLayout.NORTH);

        // Butonlar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));

        // Dizi halinde hesap makinesi tuşları
        // 0-9 rakamları, +, -, *, /, ., =, C (Temizle)
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", // Temizle butonu ekliyoruz.
        };

        // Butonlari ekleyelim
        for (String txt : buttons) {
            JButton btn = new JButton(txt);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btn.addActionListener(this);
            btn.addKeyListener(this); // Klavye dinleyicisi
            buttonPanel.add(btn);
        }

        // GridLayout 5x4 kullandigimiz icin, 5. satirda 4 hucre var.
        // Yukarida 17 eleman tanimladik, 4 hucre oldugu icin fazladan 3 bosluk var.
        // Bos hucreleri dolduruyoruz.
        for (int i = 0; i < (5*4 - buttons.length); i++) {
            JPanel emptyPanel = new JPanel();
            buttonPanel.add(emptyPanel);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Panel icin klavye odaklanmasi
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        processInput(command);
    }

    private void processInput(String input) {
        if (isDigit(input) || input.equals(".")) {
            // Rakamlari (ve .) isliyoruz
            if (input.equals(".")) {
                if (!currentInput.contains(".")) {
                    currentInput += ".";
                }
            } else {
                currentInput += input;
            }
            display.setText(currentInput);
        } else if (isOperator(input)) {
            handleOperator(input);
        } else if (input.equals("C")) {
            // Temizle
            currentInput = "";
            result = 0;
            lastOperator = "=";
            display.setText("0");
        }
    }

    private void handleOperator(String op) {
        double inputValue = 0;
        if (!currentInput.isEmpty()) {
            inputValue = Double.parseDouble(currentInput);
        }
        if (lastOperator.equals("=")) {
            result = inputValue;
        } else {
            calculate(inputValue);
        }
        lastOperator = op;
        currentInput = "";
        if (op.equals("=")) {
            display.setText(String.valueOf(result));
        } else {
            display.setText(String.valueOf(result));
        }
    }

    private void calculate(double inputValue) {
        switch (lastOperator) {
            case "+":
                result += inputValue;
                break;
            case "-":
                result -= inputValue;
                break;
            case "*":
                result *= inputValue;
                break;
            case "/":
                if (inputValue != 0)
                    result /= inputValue;
                else
                    JOptionPane.showMessageDialog(this, "Sifira bolunemez!", "Hata", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private boolean isDigit(String input) {
        return input.matches("\\d");
    }

    private boolean isOperator(String input) {
        return "+-*/=".contains(input);
    }

    // Klavye Dinleyici Metotlari

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        String key = String.valueOf(c);

        if (Character.isDigit(c) || c == '.') {
            processInput(key);
        } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=') {
            processInput(key);
        } else if (c == '\u0008') {
            // Backspace tusu: bir karakter sil.
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                display.setText(currentInput);
            }
        } else if (c == '\n') {
            // Enter tusu esittir olarak islev gorur
            processInput("=");
        } else if (c == 'c' || c == 'C') {
            // C tusu
            processInput("C");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Gerekmiyor ama zorunlu interface metodu
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Gerekmiyor ama zorunlu interface metodu
    }

    @Override
    public String getTabName() {
        return "plugin.calculator.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.PRODUCTIVITY;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}

