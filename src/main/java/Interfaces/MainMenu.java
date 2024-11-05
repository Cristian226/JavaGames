package Interfaces;

import javax.swing.*;
import java.awt.*;

public class MainMenu  extends JFrame {
    JPanel buttonsPanel = new JPanel();
    public MainMenu(){
        ImageIcon image = new ImageIcon("src/main/resources/controller.png");
        this.setTitle("MainMenu");
        this.setIconImage(image.getImage());
        this.setSize(450, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(123,50,250));
        this.setLayout(null);
        this.setVisible(true);
        this.setResizable(false);

        AddButton("FlappyBird", "src/main/resources/flappyBirdButtonIcon.jpg");
        AddButton("Snake", "src/main/resources/snakeIcon.jpg");
        AddButton("MineSweeper", "src/main/resources/controller.png");
        AddButton("X", "src/main/resources/controller.png");

        JLabel mainMenuLabel = new JLabel();
        mainMenuLabel.setText("MAIN MENU");
        mainMenuLabel.setFont(new Font("Viner Hand ITC", Font.PLAIN, 40));
        mainMenuLabel.setBounds(100,80,300,70);
        mainMenuLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        mainMenuLabel.setVisible(true);
        this.add(mainMenuLabel);

        JButton statsButton = new JButton("Stats");
        statsButton.setFont(new Font("Serif", Font.BOLD, 16));
        statsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        statsButton.setBounds(10,10,100,35);
        statsButton.setBackground(new Color(0x0A4E8C));
        statsButton.setFocusable(false);
        statsButton.setForeground(Color.BLACK);
        this.add(statsButton);

        JButton changeUser = new JButton("Log out");
        changeUser.setFont(new Font("Serif", Font.BOLD, 16));
        changeUser.setHorizontalTextPosition(SwingConstants.CENTER);
        changeUser.setBounds(325,10,100,35);
        changeUser.setBackground(new Color(0x0A4E8C));
        changeUser.setFocusable(false);
        changeUser.setForeground(Color.BLACK);
        this.add(changeUser);

        buttonsPanel.setBounds(25,200,390,140);
        buttonsPanel.setBackground(new Color(123,50,250));
        buttonsPanel.setLayout(new GridLayout(2,2,40,15));
        buttonsPanel.setVisible(true);
        this.add(buttonsPanel);

    }

    private void AddButton(String name, String path){
        JButton button = new JButton(name);
        ImageIcon buttonImage = new ImageIcon(path);
        button.setFocusable(false);
        button.setForeground(Color.BLACK);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Serif", Font.BOLD, 17));
        button.setIcon(buttonImage);
        buttonsPanel.add(button);
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }


}
