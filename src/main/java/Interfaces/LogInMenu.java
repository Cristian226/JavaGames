package Interfaces;

import javax.swing.*;
import java.awt.*;

public class LogInMenu extends JFrame {

    public LogInMenu() {
        initializeFrame();
        addComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("LogInMenu");
        setIconImage(loadIcon("src/main/resources/controller.png"));
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(123, 50, 250));
        setLayout(null);
        setResizable(false);
    }

    private Image loadIcon(String path) {
        return new ImageIcon(path).getImage();
    }

    private void addComponents() {
        addTitleLabel();
        addUsernameField();
        addPasswordField();
        addLogInButton();
    }

    private void addTitleLabel() {
        JLabel mainMenuLabel = createLabel("LOG IN MENU", new Font("Viner Hand ITC", Font.PLAIN, 40), 76, 40, 300, 70);
        add(mainMenuLabel);
    }

    private JLabel createLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setBounds(x, y, width, height);
        return label;
    }

    private void addUsernameField() {
        JTextField username = createTextField(125, 150, 200, 25);
        add(username);
    }

    private void addPasswordField() {
        JPasswordField password = createPasswordField(125, 200, 200, 25);
        add(password);
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Serif", Font.BOLD, 16));
        textField.setBounds(x, y, width, height);
        textField.setBackground(new Color(0x0A4E8C));
        textField.setForeground(Color.BLACK);
        return textField;
    }

    private JPasswordField createPasswordField(int x, int y, int width, int height) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Serif", Font.BOLD, 16));
        passwordField.setBounds(x, y, width, height);
        passwordField.setBackground(new Color(0x0A4E8C));
        passwordField.setForeground(Color.BLACK);
        return passwordField;
    }

    private void addLogInButton() {
        JButton logIn = createButton("Log in", 175, 260, e -> {
            new MainMenu();
            dispose();
        });
        add(logIn);
    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 16));
        button.setBounds(x, y, 100, 35);
        button.setBackground(new Color(0x0A4E8C));
        button.setForeground(Color.BLACK);
        button.setFocusable(false);
        button.addActionListener(actionListener);
        return button;
    }

    public static void main(String[] args) {
        LogInMenu logInMenu = new LogInMenu();
    }

}
