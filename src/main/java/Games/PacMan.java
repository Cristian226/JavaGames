package Games;

import DataBase.JDBC;
import Menus.MainMenu;
import App.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener {

    private static final int ROW_COUNT = 21;
    private static final int COLUMN_COUNT = 19;
    private static final int TILE_SIZE = 32;
    private static final int BOARD_WIDTH = COLUMN_COUNT * TILE_SIZE;
    private static final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage, powerFoodImage, scaredGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private char queuedDirection = '\0'; // '\0' indicates no direction queued

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "XFXX XXX X XXX XXFX",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X   X   X    X",
            "XXXX XXX X XXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "        bpo        ",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "XF X     P     X FX",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    private HashSet<Block> walls, foods, ghosts, powerFoods;
    private Block pacman;
    private Timer gameLoop;
    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;
    private boolean ghostsAreScared = false;
    private boolean scoreSaved = false;

    private final char[] directions = {'U', 'D', 'L', 'R'};
    private final Random random = new Random();

    public PacMan() {
        setupPanel();
        loadImages();
        loadMap();
        initializeGameLoop();
        createGameFrame();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new PacManKeyAdapter());
    }

    private void loadImages() {
        wallImage = loadImage("wall.png");
        blueGhostImage = loadImage("blueGhost.png");
        orangeGhostImage = loadImage("orangeGhost.png");
        pinkGhostImage = loadImage("pinkGhost.png");
        redGhostImage = loadImage("redGhost.png");
        pacmanUpImage = loadImage("pacmanUp.png");
        pacmanDownImage = loadImage("pacmanDown.png");
        pacmanLeftImage = loadImage("pacmanLeft.png");
        pacmanRightImage = loadImage("pacmanRight.png");
        powerFoodImage = loadImage("powerFood.png");
        scaredGhostImage = loadImage("scaredGhost.png");
    }

    private Image loadImage(String fileName) {
        return new ImageIcon("src/main/resources/PacManImages/" + fileName).getImage();
    }

    private void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();
        powerFoods = new HashSet<>();

        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                char tile = tileMap[r].charAt(c);
                int x = c * TILE_SIZE, y = r * TILE_SIZE;

                switch (tile) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, TILE_SIZE, TILE_SIZE));
                    case 'b' -> {
                        ghosts.add(new Block(blueGhostImage, x, y, TILE_SIZE, TILE_SIZE, "blueGhost"));
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    }
                    case 'o' -> {
                        ghosts.add(new Block(orangeGhostImage, x, y, TILE_SIZE, TILE_SIZE, "orangeGhost"));
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    }
                    case 'p' -> {
                        ghosts.add(new Block(pinkGhostImage, x, y, TILE_SIZE, TILE_SIZE, "pinkGhost"));
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    }
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, TILE_SIZE, TILE_SIZE, "redGhost"));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, TILE_SIZE, TILE_SIZE);
                    case ' '-> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                    case 'F' -> powerFoods.add(new Block(powerFoodImage, x + 12, y + 12, 8, 8));
                }
            }
        }
        for(Block ghost : ghosts) {
            ghost.updateDirection(randomDirection());
        }
    }

    private void initializeGameLoop() {
        gameLoop = new Timer(50, this); // 20 fps
        gameLoop.start();
    }

    private void createGameFrame() {
        JFrame frame = new JFrame("Pac Man");
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(this);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new MainMenu();
            }
        });
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        drawBlock(g, pacman);
        ghosts.forEach(ghost -> drawBlock(g, ghost));
        walls.forEach(wall -> drawBlock(g, wall));
        powerFoods.forEach(powerFood -> drawBlock(g, powerFood));
        drawFoods(g);

        if(gameOver) {
            savePacManScore();
            drawDeathScreen(g);
        }
        else drawScore(g);
    }

    private void drawBlock(Graphics g, Block block) {
        g.drawImage(block.image, block.x, block.y, block.width, block.height, null);
    }

    private void drawFoods(Graphics g) {
        g.setColor(Color.WHITE);
        foods.forEach(food -> g.fillRect(food.x, food.y, food.width, food.height));
    }

    private void drawScore(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String text = "Lives: " + lives + " Score: " + score;
        g.drawString(text, TILE_SIZE / 2, TILE_SIZE / 2);
    }

    private void drawDeathScreen(Graphics g) {
        g.setColor(new Color(255, 0, 0, 200));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.black);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 85));
        g.drawString("GAME OVER", 50, BOARD_HEIGHT / 2 - 80);
        g.setFont(new Font("Viner Hand ITC", Font.BOLD, 65));
        g.drawString("SCORE: " + (int) score, 80, BOARD_HEIGHT / 2);
        g.setFont(new Font("Viner Hand ITC", Font.PLAIN, 45));
        g.drawString("Press SPACE to Restart", 70, BOARD_HEIGHT / 2 + 120);
    }

    private void move() {
        if (queuedDirection != '\0') updatePacmanDirection();

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        checkCollisions();
        handleGhosts();
        checkFoodCollision();
    }

    private void updatePacmanDirection() {
        pacman.updateDirection(queuedDirection);

        if (pacman.direction == queuedDirection) {
            queuedDirection = '\0';
            updatePacmanImage();
        }
    }

    private void checkCollisions() {
        if (walls.stream().anyMatch(wall -> collision(pacman, wall))) {
            pacman.x -= pacman.velocityX;
            pacman.y -= pacman.velocityY;
        }

        if (pacman.x < 0) pacman.x = BOARD_WIDTH - pacman.width;
        else if (pacman.x + pacman.width > BOARD_WIDTH) pacman.x = 0;

        if (pacman.y < 0) pacman.y = BOARD_HEIGHT - pacman.height;
        else if (pacman.y + pacman.height > BOARD_HEIGHT) pacman.y = 0;
    }

    private void handleGhosts() {
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                if (ghostsAreScared) {
                    ghost.x = 0;
                    ghost.y = 225;
                    Timer resetTimer = new Timer(5000, e -> ghost.reset());
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                    score += 100;
                }
                else {
                    if (--lives == 0) {
                        gameOver = true;
                        gameLoop.stop();
                        return;
                    }
                    resetPositions();
                }
            }

            char direction = randomDirection();
            if(direction != oppositeDirection(ghost.direction)) {
                ghost.updateDirection(direction);
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            if (walls.stream().anyMatch(wall -> collision(ghost, wall))) {
                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                ghost.updateDirection(randomDirection());
            }

            if (ghost.x < 0) ghost.x = BOARD_WIDTH - ghost.width;
            else if (ghost.x + ghost.width > BOARD_WIDTH) ghost.x = 0;

            if(ghost.y < 0) ghost.y = BOARD_HEIGHT - ghost.height;
            else if(ghost.y + ghost.height > BOARD_HEIGHT) ghost.y = 0;

        }
    }

    private char randomDirection() {
        return directions[random.nextInt(directions.length)];
    }

    private char oppositeDirection(char direction) {
        return switch (direction) {
            case 'U' -> 'D';
            case 'D' -> 'U';
            case 'L' -> 'R';
            case 'R' -> 'L';
            default -> '\0'; // No direction
        };
    }

    private void checkFoodCollision() {
        foods.removeIf(food -> {
            if (collision(pacman, food)) {
                score += 10;
                return true;
            }
            return false;
        });

        powerFoods.removeIf(powerFood -> {
            if (collision(pacman, powerFood)) {
                score += 50;
                ghostsAreScared = true;
                ghosts.forEach(ghost -> ghost.image = scaredGhostImage);
                Timer powerFoodTimer = new Timer(5000, e -> {
                    ghosts.forEach(this::resetGhostImage);
                    ghostsAreScared = false;
                });
                powerFoodTimer.setRepeats(false);
                powerFoodTimer.start();
                return true;
            }
            return false;
        });

        if (foods.isEmpty() && powerFoods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void resetPositions() {
        pacman.reset();
        ghosts.forEach(ghost -> {
            ghost.reset();
            ghost.updateDirection(randomDirection());
        });
    }

    private void resetGhostImage(Block ghost) {
        switch (ghost.name) {
            case "blueGhost" -> ghost.image = blueGhostImage;
            case "orangeGhost" -> ghost.image = orangeGhostImage;
            case "pinkGhost" -> ghost.image = pinkGhostImage;
            case "redGhost" -> ghost.image = redGhostImage;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    private void updatePacmanImage() {
        switch (pacman.direction) {
            case 'U' -> pacman.image = pacmanUpImage;
            case 'D' -> pacman.image = pacmanDownImage;
            case 'L' -> pacman.image = pacmanLeftImage;
            case 'R' -> pacman.image = pacmanRightImage;
        }
    }

    private class PacManKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> queuedDirection = 'U';
                case KeyEvent.VK_DOWN -> queuedDirection = 'D';
                case KeyEvent.VK_LEFT -> queuedDirection = 'L';
                case KeyEvent.VK_RIGHT -> queuedDirection = 'R';
                case KeyEvent.VK_SPACE -> RestartGame();

            }
        }
    }

    private void RestartGame() {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            scoreSaved = false;
            ghostsAreScared = false;
            gameLoop.start();
        }
    }

    private void savePacManScore(){
        if(!scoreSaved){
            scoreSaved = true;
            JDBC.CheckAndSetHighScore(App.getUserID(),"pacman", (int) score);
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        String name;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this(image, x, y, width, height, null);
        }

        Block(Image image, int x, int y, int width, int height, String name) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
            this.name = name;
        }

        void updateDirection(char direction) {
            if (this.direction == direction) return; // Ignore if the direction is the same

            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            // Temporarily move in the new direction to check for collisions
            this.x += this.velocityX;
            this.y += this.velocityY;

            boolean hasCollision = false;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    hasCollision = true;
                    break;
                }
            }

            // Revert the position if there is a collision
            if (hasCollision) {
                this.x -= this.velocityX;
                this.y -= this.velocityY;
                this.direction = prevDirection;
                updateVelocity();
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -TILE_SIZE / 4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = TILE_SIZE / 4;
                }
                case 'L' -> {
                    this.velocityX = -TILE_SIZE / 4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = TILE_SIZE / 4;
                    this.velocityY = 0;
                }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    public static void main(String[] args) {
        new PacMan();
    }
}
