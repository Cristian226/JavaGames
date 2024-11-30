package Interfaces;

import Games.FlappyBird;
import Games.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainMenu extends JFrame {
    private JPanel buttonsPanel;

    public MainMenu() {
        initializeFrame();
        addComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("MainMenu");
        setIconImage(loadIcon("src/main/resources/controller.png"));
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

        addGameButton("Flappy Bird", "src/main/resources/flappyBirdButtonIcon.jpg");
        addGameButton("Snake", "src/main/resources/snakeIcon.jpg");
        addGameButton("Mine Sweeper", "src/main/resources/controller.png");
        addGameButton("X", "src/main/resources/controller.png");
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

        Image scaledImage = loadIcon(path).getScaledInstance(180, 70, Image.SCALE_DEFAULT);
        button.setIcon(new ImageIcon(scaledImage));
        button.setForeground(new Color(30, 13, 62));
        button.setFont(new Font("Ink Free", Font.BOLD,  24));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        buttonsPanel.add(button);
    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.ITALIC, 16));
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
                Snake snake = new Snake();
                break;
            case "Flappy Bird":
                try {
                    FlappyBird flappyBird = new FlappyBird();
                    break;
                }
                catch (Exception e) {
                    System.out.println("Error: " + e);
                }

            default:
                JFrame gameFrame = new JFrame(gameName);
                gameFrame.setSize(300, 200);
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JLabel gameLabel = createLabel("This is the " + gameName + " screen", new Font("Serif", Font.BOLD, 20), 0, 0, 300, 200);
                gameFrame.add(gameLabel);
                gameFrame.setVisible(true);

                gameFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        dispose();
                        new MainMenu();
                    }
                });
        }

        dispose();
    }

    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
    }

}
