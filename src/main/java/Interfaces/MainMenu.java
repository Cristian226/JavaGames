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
