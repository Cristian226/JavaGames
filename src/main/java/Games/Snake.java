package Games;

import Interfaces.LogInMenu;
import Interfaces.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Snake extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 475;
    static final int SCREEN_HEIGHT = 396;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 125;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    private JButton restartButton;

    public Snake() {
        createRestartButton(); // Create the restart button
        initializeGamePanel();  // Initialize game-related components (e.g., random generator)
        startGame(); // Start the game
    }

    private void createRestartButton() {
        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Serif", Font.BOLD, 25));
        restartButton.setFocusable(false);
        restartButton.setVisible(false); // Initially hide it
        restartButton.setBounds(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + SCREEN_HEIGHT /4, 200, 50);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setBackground(new Color(123, 50, 250));

        this.add(restartButton);
    }

    private void initializeGamePanel() {
        random = new Random();

        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setBackground(Color.black);
        this.setForeground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.setLayout(null);

        JFrame gameWindow = new JFrame("Snake Game");
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        gameWindow.add(this);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setVisible(true);

        gameWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameWindow.dispose();
                new MainMenu();
            }
        });

    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            drawGame(g);
        } else {
            drawGameOver(g);
        }
    }

    private void drawGame(Graphics g) {
        drawApple(g);
        drawSnake(g);
        drawScore(g);
    }

    private void drawApple(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green); // Head of the snake
            } else {
                g.setColor(new Color(45, 180, 0)); // Body of the snake
            }
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(new Color(123, 50, 250));
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        String scoreText = "Score: " + applesEaten;
        g.drawString(scoreText, (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth(scoreText)) / 2, g.getFont().getSize());
    }

    private void newApple() {
        appleX = random.nextInt((SCREEN_HEIGHT - UNIT_SIZE ) / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT - UNIT_SIZE )/ UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    private void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void drawGameOver(Graphics g) {
        restartButton.setVisible(true);
        drawScore(g);
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        String gameOverText = "Game Over";
        g.drawString(gameOverText, (SCREEN_WIDTH - getFontMetrics(g.getFont()).stringWidth(gameOverText)) / 2, SCREEN_HEIGHT / 2);
        this.repaint();
    }

    private void restartGame() {
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        running = true;

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        restartButton.setVisible(false);
        initializeGamePanel();
        startGame();
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }

    // Main method to launch the game
    public static void main(String[] args) {
        new Snake();
    }
}
