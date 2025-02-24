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
package org.example.orphan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BallBouncingGamePanel extends JPanel {
    private Timer timer;
    private double ballX, ballY, ballRadius;
    private double dx = 3, dy = 3;
    private JButton startButton, resetButton;
    private boolean isRunning = false;

    public BallBouncingGamePanel() {
        setLayout(null);

        startButton = new JButton("Start");
        resetButton = new JButton("Reset");

        startButton.setBounds(10, 10, 80, 30);
        resetButton.setBounds(100, 10, 80, 30);

        add(startButton);
        add(resetButton);

        startButton.addActionListener(e -> toggleGame());
        resetButton.addActionListener(e -> resetGame());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustBallSize();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleGame();
            }
        });

        timer = new Timer(16, e -> moveBall());
        resetGame();
    }

    private void adjustBallSize() {
        int size = Math.min(getWidth(), getHeight()) / 10;
        ballRadius = size / 2.0;
        resetBallPosition();
    }

    private void resetBallPosition() {
        ballX = getWidth() / 2.0 - ballRadius;
        ballY = getHeight() / 2.0 - ballRadius;
    }

    private void moveBall() {
        if (!isRunning) return;

        ballX += dx;
        ballY += dy;

        if (ballX <= 0 || ballX + ballRadius * 2 >= getWidth()) {
            dx = -dx;
        }
        if (ballY <= 0 || ballY + ballRadius * 2 >= getHeight()) {
            dy = -dy;
        }

        repaint();
    }

    private void toggleGame() {
        isRunning = !isRunning;
        if (isRunning) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    private void resetGame() {
        isRunning = false;
        timer.stop();
        adjustBallSize();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.fillOval((int) ballX, (int) ballY, (int) (ballRadius * 2), (int) (ballRadius * 2));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Bouncing Game");
        BallBouncingGamePanel panel = new BallBouncingGamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(panel);
        frame.setVisible(true);
    }
}
