package DataBase;

import java.net.InetAddress;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import App.App;

public class JDBC {

    public static Connection getConnection() {
        String host = "localhost";  // Default to localhost for Windows
        String ip = "192.168.0.193";  // IP for Windows when running in WSL
        // XD XD

        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("linux") && InetAddress.getByName("localhost").isReachable(500)) {
                host = ip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":3306/javagames_schema",
                    "app_user",  // MySQL user
                    "cristi04");  // MySQL password
        } catch (SQLException e) {
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

    public static void CheckAndSetHighScore(int id, String game, int gameID, int highscore) {
        String score = GetHighScore(id, game);
        if(Objects.equals(score, "No high score found")) {
            addUserActivity(gameID, "First highscore");
            SetHighScore(id, game, highscore);
            return;
        }

        int currentHighScore = Integer.parseInt(score);
        if (Objects.equals(game, "minesweeper")) {
            if (highscore < currentHighScore) {
                SetHighScore(id, game, highscore);
                addUserActivity(gameID, "New highscore");
            }
        } else {
            if (highscore > currentHighScore) {
                SetHighScore(id, game, highscore);
                addUserActivity(gameID, "New highscore");
            }
        }
    }

    public static String addUserActivity(int idGame, String action) {
        Connection connection = getConnection();
        String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String query = "INSERT INTO user_activities_table (id_user, id_game, activity_time, action) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, App.getUserID());
            preparedStatement.setInt(2, idGame);
            preparedStatement.setString(3, currentTimestamp);
            preparedStatement.setString(4, action);
            preparedStatement.executeUpdate();
            return "Activity added successfully.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error adding activity: " + e.getMessage();
        }
    }

    public static ArrayList<String> getLeaderboardData() {
        ArrayList<String> leaderboardEntries = new ArrayList<>();
        String query = "SELECT games_table.name, leaderboard_table.rank, " +
                "users_table.username, leaderboard_table.score " +
                "FROM leaderboard_table " +
                "JOIN games_table ON leaderboard_table.id_game = games_table.id_game " +
                "JOIN users_table ON leaderboard_table.id_user = users_table.id_users_table " +
                "ORDER BY leaderboard_table.id_game, leaderboard_table.rank";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String gameName = rs.getString("name");
                int rank = rs.getInt("rank");
                String username = rs.getString("username");
                int score = rs.getInt("score");
                String entry = String.format("%d - %s", score, username);
                leaderboardEntries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboardEntries;
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
