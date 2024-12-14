package Games;

import DataBase.JDBC;
import Menus.MainMenu;
import App.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper {

    private static final int TILE_SIZE = 70;
    private static final int NUM_ROWS = 8;
    private static final int NUM_COLS = 10;
    private static final int BOARD_WIDTH = NUM_COLS * TILE_SIZE;
    private static final int BOARD_HEIGHT = NUM_ROWS * TILE_SIZE;
    private static final String MINE_SYMBOL = "💣";
    private static final String FLAG_SYMBOL = "🚩";

    private JFrame frame = new JFrame("Minesweeper");
    private JLabel textLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();

    private int mineCount = 10;
    private MineTile[][] board = new MineTile[NUM_ROWS][NUM_COLS];
    private ArrayList<MineTile> mineList = new ArrayList<>();
    private Random random = new Random();

    private int tilesClicked = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private Timer timer;
    private long startTime;

    public MineSweeper() {
        initializeFrame();
        initializeTextLabel();
        initializeBoardPanel();
        placeTiles();
        setMines();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setIconImage(new ImageIcon("src/main/resources/MenuImages/mineSweeper.png").getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new MainMenu();
            }
        });
    }

    private void initializeTextLabel() {
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setText("Minesweeper: " + mineCount);
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);
    }

    private void initializeBoardPanel() {
        boardPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon("src/main/resources/MenuImages/mineSweeper.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw background image
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // add a gray transparent colour on top
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        boardPanel.setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
        frame.add(boardPanel);
    }

    private void placeTiles() {
        for (int r = 0; r < NUM_ROWS; r++) {
            for (int c = 0; c < NUM_COLS; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                configureTile(tile);
                boardPanel.add(tile);
            }
        }
    }

    private void configureTile(MineTile tile) {
        tile.setFocusable(false);
        tile.setMargin(new Insets(0, 0, 0, 0));
        tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

        tile.addMouseListener(new MyMouseAdapter());
    }

    private void handleLeftClick(MineTile tile) {
        if (!tile.getText().isEmpty()) return;

        if (mineList.contains(tile)) {
            revealMines();
            addRestartButton();
        } else {
            checkMine(tile.r, tile.c);
        }
    }

    private void handleRightClick(MineTile tile) {
        if (tile.getText().isEmpty() && tile.isEnabled()) {
            tile.setText(FLAG_SYMBOL);
            tile.setForeground(Color.RED);
            mineCount--;
        } else if (FLAG_SYMBOL.equals(tile.getText())) {
            tile.setText("");
            tile.setForeground(Color.BLACK);
            mineCount++;
        }
    }

    private void setMines() {
        int remainingMines = mineCount;
        while (remainingMines > 0) {
            int r = random.nextInt(NUM_ROWS);
            int c = random.nextInt(NUM_COLS);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                remainingMines--;
            }
        }
    }

    private void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText(MINE_SYMBOL);
        }
        gameOver = true;
        timer.cancel();
        textLabel.setText("Game Over!" + " Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
    }

    private void checkMine(int r, int c) {
        if (r < 0 || r >= NUM_ROWS || c < 0 || c >= NUM_COLS) return;

        MineTile tile = board[r][c];
        if (!tile.isOpaque()) return;

        tile.setOpaque(false);
        tile.setContentAreaFilled(false);

        tilesClicked++;

        int minesFound = countAdjacentMines(r, c);
        if (minesFound > 0) {
            switch (minesFound) {
                case 1:
                    tile.setForeground(Color.blue);
                    break;
                case 2:
                    tile.setForeground(Color.green);
                    break;
                case 3:
                    tile.setForeground(Color.red);
                    break;
                case 4:
                    tile.setForeground(new Color(0x00008B));
                    break;
                default:
                    tile.setForeground(Color.orange);
                    break;

            }

            tile.setText(String.valueOf(minesFound));

        } else {
            tile.setText("");
            revealAdjacentTiles(r, c);
        }

        if (tilesClicked == NUM_ROWS * NUM_COLS - mineList.size()) {
            saveMineSweeperScore();
            gameOver = true;
            timer.cancel();
            addRestartButton();
            textLabel.setText("Mines Cleared!" + " Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");

        }
    }

    private int countAdjacentMines(int r, int c) {
        int minesFound = 0;

        // Check all adjacent tiles
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) {
                    minesFound += countMine(r + dr, c + dc);
                }
            }
        }
        return minesFound;
    }

    private int countMine(int r, int c) {
        if (r < 0 || r >= NUM_ROWS || c < 0 || c >= NUM_COLS) return 0;
        return mineList.contains(board[r][c]) ? 1 : 0;
    }

    private void revealAdjacentTiles(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) {
                    checkMine(r + dr, c + dc);
                }
            }
        }
    }

    private void addRestartButton() {
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.addActionListener(e -> {
            resetGame();
            textPanel.remove(restartButton);
        });
        textPanel.add(restartButton, BorderLayout.EAST);
    }

    private void resetGame(){
        for (int r = 0; r < NUM_ROWS; r++) {
            for (int c = 0; c < NUM_COLS; c++) {
                MineTile tile = board[r][c];
                tile.setText("");
                tile.setOpaque(true);
                tile.setForeground(Color.black);
                tile.setContentAreaFilled(true);
            }
        }

        mineList.clear();
        mineCount = 10;
        setMines();
        tilesClicked = 0;
        gameOver = false;
        gameStarted = false;
        textLabel.setText("Mines: " + mineCount);
        timer.cancel();
    }

    private void saveMineSweeperScore(){
        JDBC.CheckAndSetHighScore(App.getUserID(), "minesweeper", (int)((System.currentTimeMillis() - startTime) / 1000));
    }

    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (gameOver) return;
            if(!gameStarted) {
                gameStarted = true;
                startTime = System.currentTimeMillis();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        textLabel.setText("Mines: " + mineCount + " Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
                    }
                }, 0, 1000);
            }

            MineTile clickedTile = (MineTile) e.getSource();

            if (e.getButton() == MouseEvent.BUTTON1) {
                handleLeftClick(clickedTile);
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                handleRightClick(clickedTile);
            }
        }
    }

    private static class MineTile extends JButton {
        int r, c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
            setOpaque(true);
            setBackground(Color.white);
            setForeground(Color.black);

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Custom background for the disabled state
            if (!isEnabled()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.black); // Example gray background for disabled tiles
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // Foreground is automatically handled by JButton
        }
    }

    public static void main(String[] args) {
        new MineSweeper();
    }
}
