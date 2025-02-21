package org.example.jpanels.games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BrickBreakerGamePanel extends JPanel {
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker Game");
        BrickBreakerGamePanel panel = new BrickBreakerGamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(panel);
        frame.setVisible(true);
    }
}
