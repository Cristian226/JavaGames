package Games;

import DataBase.JDBC;
import Menus.MainMenu;
import App.App;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Snake extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 25;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int GAME_SPEED = 80;

    private Tile snakeHead;
    private final ArrayList<Tile> snakeBody;
    private final Tile food;
    private final Random random;

    private int velocityX = 0, velocityY = 0;
    private final Timer gameLoop;
    private boolean gameOver;
    private boolean scoreSaved;

    ArrayList<Tile> walls = new ArrayList<>();

    public Snake() {
        // Set up JFrame
        JFrame frame = new JFrame("Snake");
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Set up game panel
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        this.setFocusable(true);
        addKeyListener(new SnakeKeyAdapter());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JDBC.addUserActivity(App.snakeID, "Closed Snake");
                new MainMenu();
            }
        });
        frame.add(this);
        frame.pack();
        frame.setVisible(true);

        // Initialize game components
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();
        scoreSaved = false;
        gameOver = false;
        generateWalls();
        placeFood();

        // Start game loop
        gameLoop = new Timer(GAME_SPEED, this);
        gameLoop.start();
    }

    public void generateWalls(){
        for(int i = 0; i < BOARD_WIDTH / TILE_SIZE; i++){
            walls.add(new Tile(i, 0));
            walls.add(new Tile(i, BOARD_HEIGHT / TILE_SIZE - 1));
        }
        Random random = new Random();
        for(int i = 0; i < 25; i++){
            walls.add(new Tile(random.nextInt(BOARD_WIDTH / TILE_SIZE), random.nextInt(BOARD_HEIGHT / TILE_SIZE)));
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    private void draw(Graphics g) {
        // Draw grid
        g.setColor(Color.GRAY);
        for (int i = 0; i < BOARD_WIDTH / TILE_SIZE; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, BOARD_HEIGHT);
            g.drawLine(0, i * TILE_SIZE, BOARD_WIDTH, i * TILE_SIZE);
        }

        // Draw walls
        g.setColor(Color.RED);
        for(Tile wall : walls){
            g.fill3DRect(wall.x * TILE_SIZE, wall.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);
        }

        // Draw food
        g.setColor(Color.BLUE);
        g.fill3DRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);

        // Draw snake
        g.setColor(Color.GREEN);
        g.fill3DRect(snakeHead.x * TILE_SIZE, snakeHead.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);
        for (Tile part : snakeBody) {
            g.fill3DRect(part.x * TILE_SIZE, part.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);
        }

        // Draw score or game over
        if(!gameOver){
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Score: " + snakeBody.size(), TILE_SIZE, TILE_SIZE);
        }
        else{
            saveSnakeScore();
            drawDeathScreen(g);
        }


    }

    private void drawDeathScreen(Graphics g){
        g.setColor(new Color(255, 0, 0, 180));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.black);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 85));
        g.drawString("GAME OVER", 15, BOARD_HEIGHT / 2 - 80);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 55));
        g.drawString("SCORE: " + (int) snakeBody.size(), 70, BOARD_HEIGHT / 2);
        g.setFont(new Font("Viner Hand ITC", Font.PLAIN, 45));
        g.drawString("Press any key to Restart", 35, BOARD_HEIGHT / 2 + 100);
    }

    private void placeFood() {
        food.x = random.nextInt(BOARD_WIDTH / TILE_SIZE);
        food.y = random.nextInt(BOARD_HEIGHT / TILE_SIZE);
        if(wallCollision(food))
            placeFood();
    }

    private void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        if (wallCollision(snakeHead) || bodyCollision(snakeHead)) {
            gameOver = true;
        }

        if(snakeHead.x < 0 ){
            snakeHead.x = BOARD_WIDTH / TILE_SIZE - 1;
        }
        if(snakeHead.x > BOARD_WIDTH / TILE_SIZE - 1){
            snakeHead.x = 0;
        }

        for (int i = snakeBody.size() - 1; i > 0; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }
        if (!snakeBody.isEmpty()) {
            snakeBody.getFirst().x = snakeHead.x;
            snakeBody.getFirst().y = snakeHead.y;
        }





    }

    private boolean collision(Tile a, Tile b) {
        return a.x == b.x && a.y == b.y;
    }

    private boolean wallCollision(Tile a){
        return walls.stream().anyMatch(wall -> collision(a, wall));
    }

    private boolean bodyCollision(Tile a){
        for(Tile part : snakeBody){
            if(collision(a, part)){
                return true;
            }
        }
        return false;
    }

    private void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;
        scoreSaved = false;
        walls.clear();
        generateWalls();
        gameLoop.start();
        placeFood();
    }

    private void saveSnakeScore(){
        if(!scoreSaved){
            scoreSaved = true;
            JDBC.CheckAndSetHighScore(App.getUserID(),"snake", App.snakeID, snakeBody.size());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    private class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(gameOver){
                resetGame();
                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if (velocityY != 1) {
                        velocityX = 0;
                        velocityY = -1;
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (velocityY != -1) {
                        velocityX = 0;
                        velocityY = 1;
                    }
                }
                case KeyEvent.VK_LEFT -> {
                    if (velocityX != 1) {
                        velocityX = -1;
                        velocityY = 0;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (velocityX != -1) {
                        velocityX = 1;
                        velocityY = 0;
                    }
                }
            }
        }
    }

    private static class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        new Snake();
    }
}
