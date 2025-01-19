package Menus;

import javax.swing.*;
import java.awt.*;
import DataBase.JDBC;
import App.App;

public class LogInMenu extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JLabel errorLabel;

    public LogInMenu() {
        initializeFrame();
        addComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("LogInMenu");
        setIconImage(loadIcon("src/main/resources/MenuImages/controller.png"));
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 203, 225));
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
        addAddUserButton();
        addErrorLabel();
        addUsernameLabel();
        addPasswordLabel();
    }

    private void addTitleLabel() {
        JLabel mainMenuLabel = createLabel("LOG IN MENU", new Font("Arial", Font.BOLD, 47),
                50, 5, 350, 70);
        mainMenuLabel.setForeground(Color.BLACK);
        add(mainMenuLabel);
    }

    private JLabel createLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(new Color(0xE1341E));
        label.setBounds(x, y, width, height);
        return label;
    }

    private void addUsernameLabel() {
        JLabel usernameLabel = createLabel("Username", new Font("Arial", Font.PLAIN, 23), 0, 90, 250, 35);
        usernameLabel.setForeground(Color.BLACK);
        add(usernameLabel);
    }

    private void addPasswordLabel() {
        JLabel passwordLabel = createLabel("Password", new Font("Arial", Font.PLAIN, 23), 0, 160, 250, 35);
        passwordLabel.setForeground(Color.BLACK);
        add(passwordLabel);
    }

    private void addErrorLabel(){
        errorLabel = createLabel(" ", new Font("Arial", Font.PLAIN, 25), 0, 320, 450, 25);
        errorLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        add(errorLabel);
    }

    private void addUsernameField() {
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.BOLD, 20));
        usernameField.setBounds(100, 120, 250, 35);
        usernameField.setBackground(Color.white);
        usernameField.setForeground(new Color(0x000000));
        add(usernameField);
    }

    private void addPasswordField() {
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.BOLD, 20));
        passwordField.setBounds(100, 190, 250, 35);
        passwordField.setBackground(Color.white);
        passwordField.setForeground(new Color(0x000000));
        add(passwordField);
    }

    private void addLogInButton() {
        JButton logIn = createButton("LOG IN", 240, 260, e -> {
            logIn();
        });
        add(logIn);
    }

    private void addAddUserButton(){
        JButton addUser = createButton("ADD USER", 50, 260, e -> {
            addUser();
        });
        add(addUser);
    }

    private void logIn() {
        String username;
        String password;
        try {
            username = usernameField.getText();
            password = String.valueOf(passwordField.getPassword());
        }
        catch (Exception e){
            errorLabel.setText("Please fill in all fields");
            repaint();
            return;
        }

        if(username.strip().equals("") || password.strip().equals("")){
            errorLabel.setText("Please fill in all fields");
            repaint();
            return;
        }

        if(!JDBC.CheckUser(username, password)){
            errorLabel.setText("Wrong username or password");
            repaint();
            return;
        }

        App.setUsername(username);
        App.setUserID(JDBC.GetUserId(username));
        new MainMenu();
        dispose();

    }

    private void addUser(){
        String username;
        String password;
        try {
            username = usernameField.getText();
            password = String.valueOf(passwordField.getPassword());
        }
        catch (Exception e){
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if(username.strip().equals("") || password.strip().equals("")){
            errorLabel.setText("Please fill in all fields");
            return;
        }
        if(username.contains(" ")){
            errorLabel.setText("Username cannot contain spaces");
            return;
        }
        if(password.contains(" ")){
            errorLabel.setText("Password cannot contain spaces");
            return;
        }

        if(JDBC.AddUser(username, password) == null){
            errorLabel.setText("User created successfully");
        }
        else {
            errorLabel.setText("User already exists");
        }

    }

    private JButton createButton(String text, int x, int y, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 23));
        button.setBounds(x, y, 160, 38);
        button.setBackground(new Color(0xFFFFFF));
        button.setForeground(new Color(0x000000));
        button.setFocusable(false);
        button.addActionListener(actionListener);
        return button;
    }

    public static void main(String[] args) {
        LogInMenu logInMenu = new LogInMenu();
    }

}
