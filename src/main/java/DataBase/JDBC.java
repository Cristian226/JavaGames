package DataBase;

import java.sql.*;
import java.util.Objects;

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

    /**
     * Checks if the user exists in the database
     * @param username
     * @param password
     * @return true if the user exists, false otherwise
     */
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
                "SELECT " + game + " FROM highscore_table WHERE id_highscore_table = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        if (resultSet.getString(game) != null) {
            return resultSet.getString(game);
        } else {
            return "No high score found";
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return "error";
    }
}

public static String SetHighScore(int id, String game, int highscore) {
    Connection connection = getConnection();
    try {
        String query = "UPDATE highscore_table SET " + game + " = ? WHERE id_highscore_table = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, highscore);
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        return e.toString();
    }
    return null;
}

    public static void CheckAndSetHighScore(int id, String game, int highscore) {
        String score = GetHighScore(id, game);
        if(Objects.equals(score, "No high score found")) {
            SetHighScore(id, game, highscore);
            return;
        }

        int currentHighScore = Integer.parseInt(score);
        if (Objects.equals(game, "minesweeper")) {
            if (highscore < currentHighScore) {
                SetHighScore(id, game, highscore);
            }
        } else {
            if (highscore > currentHighScore) {
                SetHighScore(id, game, highscore);
            }
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
