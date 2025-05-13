package DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import App.App;

public class JDBC {
    private static Connection connection = null;

    private static Connection getConnection() {
        // Load configuration from environment variables or App.ipAddress
        String host = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") :
                (App.ipAddress != null && !App.ipAddress.isEmpty() ? App.ipAddress : "localhost");
        String port = "3306";
        String dbName = "javagames_schema";
        String user = "app_user";
        String password = "cristi04";

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=false&tcpKeepAlive=true&connectTimeout=5000&socketTimeout=30000";

        try {
            // Reconnect if connection is null or closed
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("MySQL JDBC Driver not found", e);
                }
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to connect to: " + url);
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
            connection = null;
        }
    }

    public static String AddUser(String username, String password) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "INSERT INTO users_table (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return null;
        } catch (SQLException e) {
            return "Error adding user: " + e.getMessage();
        }
    }

    public static boolean CheckUser(String username, String password) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT * FROM users_table WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking user: " + e.getMessage());
            return false;
        }
    }

    public static int GetUserId(String username) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT id_users_table FROM users_table WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_users_table") : -1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
            return -1;
        }
    }

    public static String GetHighScore(int id, String game) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT " + game + " FROM highscore_table WHERE id_highscore_table = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getString(game) != null
                        ? rs.getString(game)
                        : "No high score found";
            }
        } catch (SQLException e) {
            System.err.println("Error getting high score: " + e.getMessage());
            return "error";
        }
    }

    public static String SetHighScore(int id, String game, int highscore) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "UPDATE highscore_table SET " + game + " = ? WHERE id_highscore_table = ?")) {
            stmt.setInt(1, highscore);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            return null;
        } catch (SQLException e) {
            System.err.println("Error setting high score: " + e.getMessage());
            return e.getMessage();
        }
    }

    public static void CheckAndSetHighScore(int id, String game, int gameID, int highscore) {
        String score = GetHighScore(id, game);
        if ("No high score found".equals(score)) {
            addUserActivity(gameID, "First highscore");
            SetHighScore(id, game, highscore);
            return;
        }
        if ("error".equals(score) || "Invalid game column".equals(score)) {
            System.err.println("Cannot update high score: " + score);
            return;
        }

        try {
            int currentHighScore = Integer.parseInt(score);
            boolean isNewHighScore = "minesweeper".equals(game)
                    ? highscore < currentHighScore
                    : highscore > currentHighScore;
            if (isNewHighScore) {
                SetHighScore(id, game, highscore);
                addUserActivity(gameID, "New highscore");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid high score format: " + score);
        }
    }

    public static String addUserActivity(int idGame, String action) {
        String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "INSERT INTO user_activities_table (id_user, id_game, activity_time, action) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, App.getUserID());
            stmt.setInt(2, idGame);
            stmt.setString(3, currentTimestamp);
            stmt.setString(4, action);
            stmt.executeUpdate();
            return "Activity added successfully.";
        } catch (SQLException e) {
            System.err.println("Error adding activity: " + e.getMessage());
            return "Error adding activity: " + e.getMessage();
        }
    }

    public static ArrayList<String> GetLeaderboardData() {
        ArrayList<String> leaderboardEntries = new ArrayList<>();
        String query = "SELECT g.name, l.rank, u.username, l.score " +
                "FROM leaderboard_table l " +
                "JOIN games_table g ON l.id_game = g.id_game " +
                "JOIN users_table u ON l.id_user = u.id_users_table " +
                "ORDER BY l.id_game, l.rank";

        try (PreparedStatement stmt = getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String entry = String.format("%d - %s", rs.getInt("score"), rs.getString("username"));
                leaderboardEntries.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
        return leaderboardEntries;
    }


    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Successfully connected to database");
            String result = AddUser("testuser", "testpass");
            System.out.println("Add user result: " + (result == null ? "Success" : result));
        } catch (SQLException e) {
            System.err.println("Test failed: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
}