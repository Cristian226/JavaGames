package DataBase;

import java.sql.*;

public class JDBC {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/javagames_schema",
                    "root",
                    "cristi04");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String AddUser(String username, String password) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users_table (username, password) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return e.toString();

        }
        return null;
    }

    public static boolean CheckUser(String username, String password) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM users_table WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public static int GetUserId(String username) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id_users_table FROM users_table WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id_users_table");
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1;
        }
    }

    public static String GetHighScore(int id, String game) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT =? FROM users_table WHERE id = ?");
            preparedStatement.setString(1, game + "highscore");
            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("highscore");
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public static String SetHighScore(int id, String game, int highscore) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE users_table SET ? = ? WHERE id = ?");
            preparedStatement.setString(1, game + "highscore");
            preparedStatement.setString(2, String.valueOf(highscore));
            preparedStatement.setString(3, String.valueOf(id));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        }
        return null;
    }

    public static void CheckAndSetHighScore(int id, String game, int highscore) {
        int currentHighScore = Integer.parseInt(GetHighScore(id, game));
        if (highscore > currentHighScore) {
            SetHighScore(id, game, highscore);
        }
    }


    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/javagames_schema",
                    "root",
                    "cristi04");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users_table");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                System.out.println(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
