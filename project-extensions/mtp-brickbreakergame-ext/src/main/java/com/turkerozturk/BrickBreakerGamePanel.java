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
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BrickBreakerGamePanel extends JPanel implements PanelPlugin {
    private Timer timer;
    private double ballX, ballY, ballRadius;
    private double dx = 3, dy = -3, speedRatio = 1.5;
    private double paddleX, paddleWidth;
    private final double paddleHeight = 10;
    private List<Rectangle> bricks;
    private boolean isRunning = false;


    public BrickBreakerGamePanel() {
        setLayout(null);



        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustSizes();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                paddleX = e.getX() - paddleWidth / 2;
                repaint();
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

    private void adjustSizes() {
        ballRadius = Math.min(getWidth(), getHeight()) / 40.0;
        speedRatio = Math.min(getWidth(), getHeight()) / 120.0;
        paddleWidth = getWidth() / 5.0;
        paddleX = getWidth() / 2.0 - paddleWidth / 2;

        resetBricks();
        resetBallPosition();
    }

    private void resetBallPosition() {
        ballX = getWidth() / 2.0 - ballRadius;
        ballY = getHeight() - 100;
    }

    private void resetBricks() {
        bricks = new ArrayList<>();
        int rows = 5, cols = 8;
        int brickWidth = getWidth() / cols;
        int brickHeight = (int) (10 * (Math.min(getWidth(), getHeight()) / 120.0));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                bricks.add(new Rectangle(col * brickWidth, row * brickHeight + 50, brickWidth - 5, brickHeight - 5));
            }
        }
    }

    private void moveBall() {
        if (!isRunning) return;

        ballX += dx * speedRatio;
        ballY += dy * speedRatio;

        if (ballX <= 0 || ballX + ballRadius * 2 >= getWidth()) {
            dx = -dx;
        }
        if (ballY <= 0) {
            dy = -dy;
        }
        if (ballY + ballRadius * 2 >= getHeight()) {
            resetGame();
           // isRunning = false;
           // timer.stop();
        }

        Rectangle ballRect = new Rectangle((int) ballX, (int) ballY, (int) (ballRadius * 2), (int) (ballRadius * 2));
        Rectangle paddleRect = new Rectangle((int) paddleX, getHeight() - 30, (int) paddleWidth, (int) paddleHeight);

        if (ballRect.intersects(paddleRect)) {
            // Topun paddle'a çarptığı x noktası
            double collisionPoint = ballX + ballRadius;
            // Paddle'ın orta noktası
            double paddleCenter = paddleX + paddleWidth / 2.0;
            // Orta noktadan uzaklık (normalize edilmiş)
            double distanceFromCenter = (collisionPoint - paddleCenter) / (paddleWidth / 2.0);

            // dx'i güncelle (orta noktadan uzaklaştıkça daha fazla yön değiştir)
            dx += distanceFromCenter * 2; // 2 katsayısı çarpışmanın etkisini artırır, gerekirse ayarlanabilir.

            // Çarpışma sonrası dy yön değiştirir
            dy = -dy;
        }

        bricks.removeIf(brick -> {
            if (brick.intersects(ballRect)) {
                dy = -dy;
                return true;
            }
            return false;
        });

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
        adjustSizes();
        dx = 3;
        dy = -3;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        g.fillOval((int) ballX, (int) ballY, (int) (ballRadius * 2), (int) (ballRadius * 2));

        g.setColor(Color.RED);
        for (Rectangle brick : bricks) {
            g.fillRect(brick.x, brick.y, brick.width, brick.height);
        }

        g.setColor(Color.BLACK);
        g.fillRect((int) paddleX, getHeight() - 30, (int) paddleWidth, (int) paddleHeight);
    }


    @Override
    public String getTabName() {
        return "plugin.brick.breaker.game.title";
    }

    @Override
    public JPanel getPanel() {
        return new BrickBreakerGamePanel();
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.FUN;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
