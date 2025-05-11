package Menus;

import DataBase.JDBC;
import Games.FlappyBird;
import Games.MineSweeper;
import Games.PacMan;
import Games.Snake;
import App.App;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainMenu extends JFrame {
    private JPanel buttonsPanel;
    private JPanel statsPanel;
    private JPanel leaderboardPanel;


    public MainMenu() {
        initializeFrame();
        addComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("MainMenu");
        setIconImage(loadIcon("controller.png"));
        pack();
        setMinimumSize(new Dimension(450, 400));
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 203, 225));
        setLayout(null);
        setResizable(false);
        pack();
    }

    private void addComponents() {
        addTitleLabel();
        addNavigationButtons();
        addGameButtons();
        addStatsPanel();
        addLeaderboardPanel();
    }

    private Image loadIcon(String imageName) {
        try {
            // Use getResourceAsStream for JAR compatibility
            InputStream imgStream = getClass().getResourceAsStream("/MenuImages/" + imageName);
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

    private void addTitleLabel() {
        JLabel mainMenuLabel = createLabel("MAIN MENU", new Font("Arial", Font.BOLD, 45),
                75, 80, 300, 70);
        mainMenuLabel.setForeground(Color.BLACK);
        add(mainMenuLabel);
    }

    private JLabel createLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        return label;
    }

    private void addNavigationButtons() {
        JButton statsButton = createButton("STATS", 10, 10, e -> openGameFrame("Stats"));
        add(statsButton);

        JButton leaderBoard = createButton("LEADERS", 165, 10, e -> openGameFrame("Leaderboard"));
        add(leaderBoard);

        JButton logOutButton = createButton("LOG OUT", 315, 10, e -> {
            new LogInMenu();
            dispose();
        });
        add(logOutButton);
    }

    private void addGameButtons() {
        buttonsPanel = createPanel(25, 200, 390, 140, new GridLayout(2, 2, 40, 15));
        add(buttonsPanel);

        addGameButton("Flappy Bird", "flappyBirdButtonIcon.jpg");
        addGameButton("Snake", "snakeIcon.jpg");
        addGameButton("Mine Sweeper", "mineSweeperAirView.jpg");
        addGameButton("PacMan", "pacman_img.png");
    }

    private void addGameButton(String name, String path) {
        JButton button = createButton(name, 0, 0, e -> openGameFrame(name));
        button.setBackground(new Color(6, 179, 253));
        Image scaledImage = loadIcon(path).getScaledInstance(180, 70, Image.SCALE_DEFAULT);
        button.setIcon(new ImageIcon(scaledImage));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN,  22));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);

        buttonsPanel.add(button);
    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setBounds(x, y, 110, 35);
        button.setBackground(Color.white);
        button.setForeground(new Color(0x000000));
        button.setFocusable(false);
        button.addActionListener(actionListener);
        return button;
    }

    private void openGameFrame(String gameName) {
        switch (gameName){
            case "Snake":
                JDBC.addUserActivity(App.snakeID, "Started Snake");
                new Snake();
                dispose();
                break;
            case "Flappy Bird":
                JDBC.addUserActivity(App.flappyBirdID, "Started FlappyBird");
                new FlappyBird();
                dispose();
                break;
            case "PacMan":
                JDBC.addUserActivity(App.pacManID, "Started PacMan");
                new PacMan();
                dispose();
                break;
            case "Mine Sweeper":
                JDBC.addUserActivity(App.mineSweeperID, "Started MineSweeper");
                new MineSweeper();
                dispose();
                break;
            case "Stats":
                // hide this frame's components
                swapViewToMainMenu(true);
                statsPanel.setVisible(true);
                break;
            case "Leaderboard":
                // hide this frame's components
                swapViewToMainMenu(true);
                leaderboardPanel.setVisible(true);
                break;

        }

    }

    private void swapViewToMainMenu(boolean value) {
        for (Component component : getContentPane().getComponents()) {
            component.setVisible(!value);
        }
        for(Component component: statsPanel.getComponents()){
            component.setVisible(value);
        }
        for(Component component: leaderboardPanel.getComponents()){
            component.setVisible(value);
        }
    }

    private void addStatsPanel() {
        statsPanel = createPanel(0, 0, 450, 400, null);
        statsPanel.setBounds(0,0,450,400);
        statsPanel.setLayout(null);
        statsPanel.setBackground(new Color(30, 203, 225));
        statsPanel.setVisible(false);

        JLabel statsLabel = createLabel("HIGHSCORES " + App.getUsername(), new Font("Arial", Font.BOLD, 30),
                0, 10, 450, 55);
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsLabel.setForeground(new Color(0xAA1401));
        JLabel flappyBirdHighScore = createLabel("Flappy Bird: " + JDBC.GetHighScore(App.getUserID(), "flappybird") + " points",
                new Font("Arial", Font.BOLD, 23),
                20, 100, 400, 50);

        JLabel snakeHighScore = createLabel("Snake: " + JDBC.GetHighScore(App.getUserID(), "snake") + " points",
                new Font("Arial", Font.BOLD, 23),
                20, 150, 400, 50);

        JLabel pacManHighScore = createLabel("PacMan: " + JDBC.GetHighScore(App.getUserID(), "pacman") + " points",
                new Font("Arial", Font.BOLD, 23),
                20, 200, 400, 50);

        JLabel mineSweeperHighScore = createLabel("MineSweeper: " + JDBC.GetHighScore(App.getUserID(), "minesweeper") + " seconds",
                new Font("Arial", Font.BOLD, 23)
                , 20, 250, 400, 50);

        JButton backButton = createButton("BACK", 170, 320, e -> {
            statsPanel.setVisible(false);
            swapViewToMainMenu(false);
        });

        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        flappyBirdHighScore.setHorizontalAlignment(SwingConstants.LEFT);
        snakeHighScore.setHorizontalAlignment(SwingConstants.LEFT);
        pacManHighScore.setHorizontalAlignment(SwingConstants.LEFT);
        mineSweeperHighScore.setHorizontalAlignment(SwingConstants.LEFT);

        statsPanel.add(statsLabel);
        statsPanel.add(flappyBirdHighScore);
        statsPanel.add(snakeHighScore);
        statsPanel.add(pacManHighScore);
        statsPanel.add(mineSweeperHighScore);
        statsPanel.add(backButton);

        add(statsPanel);
    }

    private void addLeaderboardPanel() {
        leaderboardPanel = createPanel(0, 0, 450, 400, null);
        leaderboardPanel.setBounds(0, 0, 450, 400);
        leaderboardPanel.setLayout(null);
        leaderboardPanel.setBackground(new Color(30, 203, 225));
        leaderboardPanel.setVisible(false);

        JLabel leaderboardLabel = createLabel("LEADERBOARD", new Font("Arial", Font.BOLD, 30),
                0, 8, 450, 55);
        leaderboardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardLabel.setForeground(new Color(0xAA1401));

        ArrayList<String> leaderboardEntries = JDBC.getLeaderboardData();
        int yPositionLeft = 80;
        int yPositionRight = 80;
        int xPositionLeft = 10;
        int xPositionRight = 240;
        int gameCounter = 0;

        for (String entry : leaderboardEntries) {
            JLabel entryLabel;
            if(gameCounter == 0){
                entryLabel = createLabel("  Flappy Bird", new Font("Arial", Font.BOLD, 21),
                        xPositionLeft, yPositionLeft, 200, 25);
                yPositionLeft += 30;
                entryLabel.setHorizontalAlignment(SwingConstants.LEFT);
                leaderboardPanel.add(entryLabel);
            }
            if(gameCounter == 3){
                yPositionLeft += 30;
                entryLabel = createLabel("  Snake", new Font("Arial", Font.BOLD, 21),
                        xPositionLeft, yPositionLeft, 200, 25);
                yPositionLeft += 30;
                entryLabel.setHorizontalAlignment(SwingConstants.LEFT);
                leaderboardPanel.add(entryLabel);
            }
            if(gameCounter == 6){
                entryLabel = createLabel("  PacMan", new Font("Arial", Font.BOLD, 21),
                        xPositionRight, yPositionRight, 200, 25);
                yPositionRight += 30;
                entryLabel.setHorizontalAlignment(SwingConstants.LEFT);
                leaderboardPanel.add(entryLabel);
            }
            if(gameCounter == 9){
                yPositionRight += 30;
                entryLabel = createLabel("  Mine Sweeper", new Font("Arial", Font.BOLD, 21),
                        xPositionRight, yPositionRight, 200, 25);
                yPositionRight += 30;
                entryLabel.setHorizontalAlignment(SwingConstants.LEFT);
                leaderboardPanel.add(entryLabel);
            }
            if (gameCounter < 6) {
                entryLabel = createLabel(entry, new Font("Arial", Font.PLAIN, 19),
                        xPositionLeft, yPositionLeft, 200, 25);
                yPositionLeft += 20;
            } else {
                entryLabel = createLabel(entry, new Font("Arial", Font.PLAIN, 19),
                        xPositionRight, yPositionRight, 200, 25);
                yPositionRight += 20;
            }
            entryLabel.setHorizontalAlignment(SwingConstants.LEFT);
            leaderboardPanel.add(entryLabel);
            gameCounter++;
        }

        JButton backButton = createButton("BACK", 170, 320, e -> {
            leaderboardPanel.setVisible(false);
            swapViewToMainMenu(false);
        });

        leaderboardPanel.add(leaderboardLabel);
        leaderboardPanel.add(backButton);

        add(leaderboardPanel);
    }


    private JPanel createPanel(int x, int y, int width, int height, LayoutManager layout) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBackground(new Color(30, 203, 225));
        panel.setLayout(layout);
        return panel;
    }

    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
    }

}
