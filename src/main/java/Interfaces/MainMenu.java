package Interfaces;

import DataBase.JDBC;
import Games.FlappyBird;
import Games.MineSweeper;
import Games.PacMan;
import Games.Snake;
import org.example.App;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private JPanel buttonsPanel;
    private JPanel statsPanel;


    public MainMenu() {
        initializeFrame();
        addComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("MainMenu");
        setIconImage(loadIcon("src/main/resources/MenuImages/controller.png"));
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(123, 50, 250));
        setLayout(null);
        setResizable(false);
    }

    private void addComponents() {
        addTitleLabel();
        addNavigationButtons();
        addGameButtons();
        addStatsPanel();
    }

    private Image loadIcon(String path) {
        return new ImageIcon(path).getImage();
    }

    private void addTitleLabel() {
        JLabel mainMenuLabel = createLabel("MAIN MENU", new Font("Viner Hand ITC", Font.PLAIN, 40),
                75, 80, 300, 70);
        add(mainMenuLabel);
    }

    private JLabel createLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        return label;
    }

    private void addNavigationButtons() {
        JButton statsButton = createButton("Stats", 10, 10, e -> openGameFrame("Stats"));
        add(statsButton);

        JButton logOutButton = createButton("Log out", 325, 10, e -> {
            new LogInMenu();
            dispose();
        });
        add(logOutButton);
    }

    private void addGameButtons() {
        buttonsPanel = createPanel(25, 200, 390, 140, new GridLayout(2, 2, 40, 15));
        add(buttonsPanel);

        addGameButton("Flappy Bird", "src/main/resources/MenuImages/flappyBirdButtonIcon.jpg");
        addGameButton("Snake", "src/main/resources/MenuImages/snakeIcon.jpg");
        addGameButton("Mine Sweeper", "src/main/resources/MenuImages/mineSweeperAirView.jpg");
        addGameButton("PacMan", "src/main/resources/MenuImages/pacman_img.png");
    }

    private JPanel createPanel(int x, int y, int width, int height, LayoutManager layout) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBackground(new Color(123, 50, 250));
        panel.setLayout(layout);
        return panel;
    }

    private void addGameButton(String name, String path) {
        JButton button = createButton(name, 0, 0, e -> openGameFrame(name));
        button.setBackground(new Color(6, 179, 253));
        Image scaledImage = loadIcon(path).getScaledInstance(180, 70, Image.SCALE_DEFAULT);
        button.setIcon(new ImageIcon(scaledImage));
        button.setForeground(new Color(4, 24, 103));
        button.setFont(new Font("Ink Free", Font.BOLD,  23));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);

        buttonsPanel.add(button);
    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Viner Hand ITC", Font.PLAIN, 16));
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setBounds(x, y, 100, 35);
        button.setBackground(new Color(0x0A4E8C));
        button.setFocusable(false);
        button.setForeground(Color.BLACK);
        button.addActionListener(actionListener);
        return button;
    }

    private void openGameFrame(String gameName) {
        switch (gameName){
            case "Snake":
                new Snake();
                dispose();
                break;
            case "Flappy Bird":
                new FlappyBird();
                dispose();
                break;
            case "PacMan":
                new PacMan();
                dispose();
                break;
            case "Mine Sweeper":
                new MineSweeper();
                dispose();
                break;
            case "Stats":
                // hide this frame's components
                swapViewToMainMenu(true);
                statsPanel.setVisible(true);
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
    }

    private void addStatsPanel() {
        statsPanel = createPanel(0, 0, 450, 400, null);
        statsPanel.setBounds(0,0,450,400);
        statsPanel.setLayout(null);
        statsPanel.setBackground(new Color(123, 50, 250));
        statsPanel.setVisible(false);
        
        JLabel statsLabel = createLabel("HIGHSCORES - " + App.getUsername(), new Font("Viner Hand ITC", Font.BOLD, 27),
                0, 10, 450, 50);
        JLabel flappyBirdHighScore = createLabel("Flappy Bird: " + JDBC.GetHighScore(App.getUserID(),
                        "flappybird"), new Font("Viner Hand ITC", Font.BOLD, 20),
                20, 100, 400, 50);
        JLabel snakeHighScore = createLabel("Snake: " + JDBC.GetHighScore(App.getUserID(),
                        "snake"), new Font("Viner Hand ITC", Font.BOLD, 20),
                20, 150, 400, 50);
        JLabel pacManHighScore = createLabel("PacMan: " + JDBC.GetHighScore(App.getUserID(),
                        "pacman"), new Font("Viner Hand ITC", Font.BOLD, 20),
                20, 200, 400, 50);
        JLabel mineSweeperHighScore = createLabel("MineSweeper: " + JDBC.GetHighScore(App.getUserID(),
                        "minesweeper"), new Font("Viner Hand ITC", Font.BOLD, 20)
                , 20, 250, 400, 50);
        JButton backButton = createButton("Back", 175, 320, e -> {
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



    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
    }

}
