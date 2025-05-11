package Games;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import DataBase.JDBC;
import Menus.MainMenu;
import App.App;

public class FlappyBird extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 360;
    private static final int BOARD_HEIGHT = 640;
    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;
    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final int GRAVITY = 1;
    private static int VELOCITY_X = -3;  // Horizontal (Pipes) Speed
    private static final int VELOCITY_Y = -12;  // Jump Power
    private static final int PIPE_INTERVAL = 1400;  // Time between pipes
    private static final int GAME_LOOP_INTERVAL = 1000 / 140;  // 140 FPS
    private static int BIRD_MAX_GRAVITY = 6;

    private Image backgroundImg;
    private Image birdImg;
    private Image topPipeImg;
    private Image bottomPipeImg;

    private Bird bird;
    private ArrayList<Pipe> pipes;

    private Timer gameLoop;
    private Timer placePipeTimer;
    private boolean gameOver;
    private double score;
    private boolean gameStarted;
    private boolean scoreSaved;
    StringBuilder cheatCodeBuffer = new StringBuilder();  // Buffer for cheat code :)

    public FlappyBird() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());

        loadImages();
        initializeGame();
        createGameFrame();
    }

    private void loadImages() {
        backgroundImg = loadIcon("flappybirdbg.png");
        birdImg = loadIcon("flappybird.png");
        topPipeImg = loadIcon("toppipe.png");
        bottomPipeImg = loadIcon("bottompipe.png");
    }

    private Image loadIcon(String imageName) {
        try {
            // Use getResourceAsStream for JAR compatibility
            InputStream imgStream = getClass().getResourceAsStream("/FlappyBirdImages/" + imageName);
            if (imgStream == null) {
                System.err.println("Image not found: /MenuImages/" + imageName);
                return null;
            }
            return ImageIO.read(imgStream);
        } catch (IOException e) {
            System.err.println("Error loading image: " + imageName);
            e.printStackTrace();
            return null;
        }
    }

    private void initializeGame() {
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();
        gameOver = false;
        score = 0;
        gameStarted = false;
        scoreSaved = false;

        placePipeTimer = new Timer(PIPE_INTERVAL, e -> placePipes());
        gameLoop = new Timer(GAME_LOOP_INTERVAL, this);
    }

    private void createGameFrame() {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JDBC.addUserActivity(App.flappyBirdID, "Closed Flappy Bird");
                new MainMenu();
            }
        });
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Happens every frame (140 FPS)
        if (gameStarted) {
            move();
            repaint();
            if (gameOver) {
                saveFlappyBirdScore();
                gameOverAnimation();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backgroundImg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);

        for (Pipe pipe : pipes) {
            g2d.drawImage(pipe.img, pipe.x, pipe.y, PIPE_WIDTH, PIPE_HEIGHT, null);
        }

        if (gameStarted) {
            double angle = Math.toRadians(Math.min(bird.velocityY * 3 - 20, 40)); // Calculate rotation angle
            if(gameOver) {
                angle = Math.toRadians(70);
            }
            g2d.rotate(angle, bird.x + BIRD_WIDTH / 2, bird.y + BIRD_HEIGHT / 2); // Rotate around the bird's center
            g2d.drawImage(bird.img, bird.x, bird.y, BIRD_WIDTH, BIRD_HEIGHT, null);
            g2d.rotate(-angle, bird.x + BIRD_WIDTH / 2, bird.y + BIRD_HEIGHT / 2); // Reset rotation
        } else {
            g2d.drawImage(bird.img, bird.x, -BIRD_HEIGHT, BIRD_WIDTH, BIRD_HEIGHT, null);
        }

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            drawDeathScreen(g2d);
        } else if (!gameStarted) {
            drawStartScreen(g2d);
        } else {
            g2d.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 33));
        g.drawString("Press SPACE to start", 15, BOARD_HEIGHT / 2 - 70);
    }

    private void drawDeathScreen(Graphics g) {
        g.setColor(new Color(255, 0, 0, 180));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.black);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 55));
        g.drawString("GAME OVER", 15, BOARD_HEIGHT / 2 - 60);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 35));
        g.drawString("SCORE: " + (int) score, 70, BOARD_HEIGHT / 2);
        g.setFont(new Font("Viner Hand ITC", Font.PLAIN, 28));
        g.drawString("Press SPACE to Restart", 35, BOARD_HEIGHT / 2 + 90);
    }

    private void move() {
        bird.y += Math.min(bird.velocityY += GRAVITY, BIRD_MAX_GRAVITY);
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += VELOCITY_X;

            if (!pipe.passed && bird.x > pipe.x + PIPE_WIDTH) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > BOARD_HEIGHT - BIRD_HEIGHT) {
            gameOver = true;
        }
    }

    private void gameOverAnimation() {
        placePipeTimer.stop();
        VELOCITY_X = 0; // Stop pipes from moving, will reset when game restarts
        BIRD_MAX_GRAVITY = 10;  // Increase gravity to make bird fall faster
        try{
            removeKeyListener(getKeyListeners()[0]); // Prevent moving bird after game over
        }
        catch (Exception exception){} // Prevent errors when removing key listener
        if(bird.y > BOARD_HEIGHT - BIRD_HEIGHT) {
            gameLoop.stop();
            addKeyListener(new MyKeyAdapter()); // Add key listener back
        }
    }

    private void placePipes() {
        int randomPipeY = (int) (Math.random() * (BOARD_HEIGHT / 2));
        int openingSpace = BOARD_HEIGHT / 4;

        Pipe topPipe = new Pipe(topPipeImg, randomPipeY - PIPE_HEIGHT);
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottomPipeImg, randomPipeY + openingSpace);
        pipes.add(bottomPipe);
    }

    private boolean collision(Bird a, Pipe b) {
        return a.x < b.x + PIPE_WIDTH &&
                a.x + BIRD_WIDTH > b.x &&
                a.y < b.y + PIPE_HEIGHT &&
                a.y + BIRD_HEIGHT > b.y;
    }

    private void resetGame() {
        bird.y = BOARD_HEIGHT / 2;
        bird.velocityY = 0;
        VELOCITY_X = -3;
        BIRD_MAX_GRAVITY = 6;

        gameOver = false;
        gameStarted = false;
        scoreSaved = false;
        score = 0;
        pipes.clear();

        repaint();
    }

    private void saveFlappyBirdScore() {
        if(!scoreSaved){
            scoreSaved = true;
            JDBC.CheckAndSetHighScore(App.getUserID(), "flappybird", App.flappyBirdID, (int) score);
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!gameStarted) {
                    gameStarted = true;
                    gameLoop.start();
                    placePipeTimer.start();
                }
                bird.velocityY = VELOCITY_Y; // Reset bird's velocity
                if (gameOver) {
                    resetGame();
                }
            }
            else if (Character.isAlphabetic(e.getKeyChar())) { // Fun cheat code :)
                cheatCodeBuffer.append(e.getKeyChar());
                if (cheatCodeBuffer.toString().contains("cheatsxd")) {
                    score += 100;
                    cheatCodeBuffer.setLength(0);
                }
                if (cheatCodeBuffer.length() > 8) {
                    cheatCodeBuffer.deleteCharAt(0);
                }
            }


        }

    }


    public static void main(String[] args) {
        new FlappyBird();
    }

    private static class Bird {
        int x = BOARD_WIDTH / 8;
        int y = BOARD_HEIGHT / 2;
        int velocityY = 0;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    private static class Pipe {
        int x = BOARD_WIDTH;
        int y;
        boolean passed = false;
        Image img;

        Pipe(Image img, int y) {
            this.img = img;
            this.y = y;
        }
    }
}