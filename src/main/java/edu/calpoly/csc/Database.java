package edu.calpoly.csc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Database {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/username";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    private static final Database instance = new Database();

    // Instance variables
    private Connection connection;
    private boolean connected = false;

    public static Database getInstance() {
        return instance;
    }

    private Database() {
        connect();
    }

    public boolean isConnected() {
        return connected;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            connected = true;
        } catch (SQLException e) {
            e.printStackTrace();
            connected = false;
        }
    }

    // ======================================================================================================
    // Query methods below:

    public boolean isValidCredentials(String username, String password) {
        if (!connected) {
            connect();
        }

        try {
            String query = "SELECT * FROM Mixtapes_Users WHERE username = ? AND passcode = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if there's at least one matching row
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean isUsernameAvailable(String username) {
        if (!connected) {
            connect();
        }

        try {
            String query = "SELECT * FROM Mixtapes_Users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return !resultSet.next(); // Returns true if the username is not already in the table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean registerUser(String username, String password, String token) {
        if (!connected) {
            connect();
        }

        try {
            String query = "INSERT INTO Mixtapes_Users (username, passcode, auth_token) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, token);
    
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was inserted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public ArrayList<Song> getPlaylistEntries(int playlistId) {
        if (!connected) {
            connect();
        }

        try {
            String query = "SELECT * FROM Mixtapes_Entries me, Mixtapes_Songs ms WHERE me.pid = ? AND me.sid = ms.sid";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<Song> songs = new ArrayList<>();
                while (resultSet.next()) {
                    songs.add(
                        new Song(
                            resultSet.getInt("sid"),
                            resultSet.getString("title"),
                            resultSet.getString("artist"),
                            resultSet.getString("album"),
                            resultSet.getString("duration"),
                            resultSet.getString("genre")
                        )
                    );
                }
                
                return songs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Handle the exception appropriately in your application
        }
    }

    public ArrayList<Song> getSongs() {
        if (!connected) {
            connect();
        }

        try {
            String query = "SELECT * FROM Mixtapes_Songs";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<Song> songs = new ArrayList<>();
                while (resultSet.next()) {
                    songs.add(
                        new Song(
                            resultSet.getInt("sid"),
                            resultSet.getString("title"),
                            resultSet.getString("artist"),
                            resultSet.getString("album"),
                            resultSet.getString("duration"),
                            resultSet.getString("genre")
                        )
                    );
                }
                
                return songs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Handle the exception appropriately in your application
        }
    }

    public boolean addPlaylistEntry(int playlistId, int songID) {
        if (!connected) {
            connect();
        }

        try {
            String query = "INSERT INTO Mixtapes_Entries (pid, sid) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                preparedStatement.setInt(2, songID);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was inserted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean removePlaylistEntry(int playlistId, int entryId) {
        if (!connected) {
            connect();
        }

        try {
            String query = "DELETE FROM Mixtapes_Entries WHERE pid = ? AND sid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                preparedStatement.setInt(2, entryId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean getPlaylistVisibility(int playlistId) {
        if (!connected) {
            connect();
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT isPublic FROM Mixtapes_Playlists WHERE pid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getBoolean("isPublic");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false or handle appropriately if visibility cannot be determined
    }

    public boolean addCollaborator(int playlistId, String collaboratorUsername) {
        if (!connected) {
            connect();
        }

        try {
            // Check if the collaborator exists in the Users table
            String checkUserQuery = "SELECT uid FROM Mixtapes_Users WHERE username = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                checkUserStatement.setString(1, collaboratorUsername);
                ResultSet userResult = checkUserStatement.executeQuery();
                if (userResult.next()) {
                    int collaboratorId = userResult.getInt("uid");

                    // Add collaborator to the Collaborators table
                    String addCollaboratorQuery = "INSERT INTO Mixtapes_Collaborators (pid, uid) VALUES (?, ?)";
                    try (PreparedStatement addCollaboratorStatement = connection
                            .prepareStatement(addCollaboratorQuery)) {
                        addCollaboratorStatement.setInt(1, playlistId);
                        addCollaboratorStatement.setInt(2, collaboratorId);
                        int rowsAffected = addCollaboratorStatement.executeUpdate();
                        return rowsAffected > 0; // Returns true if at least one row was inserted
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false or handle appropriately if collaborator cannot be added
    }

    public boolean removeCollaborator(int playlistId, int collaboratorId) {
        if (!connected) {
            connect();
        }

        try {
            String query = "DELETE FROM Mixtapes_Collaborators WHERE pid = ? AND uid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                preparedStatement.setInt(2, collaboratorId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false or handle appropriately if collaborator cannot be removed
    }

    public boolean togglePlaylistVisibility(int playlistId, boolean isPublic) {
        if (!connected) {
            connect();
        }

        try {
            String query = "UPDATE Mixtapes_Playlists SET isPublic = ? WHERE pid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, isPublic);
                preparedStatement.setInt(2, playlistId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was updated
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public ArrayList<User> viewCollaborators(int playlistId) {
        if (!connected) {
            connect();
        }

        try {
            String query = "SELECT username FROM Mixtapes_Users " +
                    "JOIN Mixtapes_Collaborators ON Mixtapes_Users.uid = Mixtapes_Collaborators.uid " +
                    "WHERE Mixtapes_Collaborators.pid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<User> collaborators = new ArrayList<>();
                while (resultSet.next()) {
                    collaborators.add(new User(resultSet.getString("username")));
                }
                return collaborators;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Default to null or handle appropriately if collaborators cannot be viewed
    }

    public ArrayList<Playlist> getPlaylists(String username) {
        if (!connected) {
            connect();
        }

        ArrayList<Playlist> playlists = new ArrayList<>();

        try {
            String query = "SELECT mp.pid as id, mu2.username as creator, mp.pName as name, mp.isPublic as public, mu2.uid != mu.uid as shared " + 
            "FROM Mixtapes_Playlists mp, Mixtapes_Users mu, Mixtapes_Users mu2 " + 
            "WHERE mu2.uid = mp.uid AND ((mu.username = ? AND mu.uid in (SELECT uid FROM Mixtapes_Collaborators mc WHERE mc.pid = mp.pid)) OR (mp.uid = mu.uid AND mu.username = ?));";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Playlist p = new Playlist(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("creator"),
                            resultSet.getBoolean("public"),
                            resultSet.getBoolean("shared")
                        );
                    System.out.println(p.getPlName() + " " + p.getPlCreator() + " " + p.getPlID() + " " + p.isPlPublic());
                    playlists.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }

    public boolean createPlaylist(String username, String playlistName, boolean isPublic) {
        if (!connected) {
            connect();
        }

        try {
            String query = "INSERT INTO Mixtapes_Playlists (uid, pName, isPublic) VALUES ((SELECT uid FROM Mixtapes_Users WHERE username = ?), ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, playlistName);
                preparedStatement.setBoolean(3, isPublic);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was inserted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean deletePlaylist(int playlistId) {
        if (!connected) {
            connect();
        }

        try {
            String query = "DELETE FROM Mixtapes_Playlists WHERE pid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, playlistId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }

    public boolean renamePlaylist(int playlistId, String newName) {
        if (!connected) {
            connect();
        }

        try {
            String query = "UPDATE Mixtapes_Playlists SET pName = ? WHERE pid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setInt(2, playlistId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if at least one row was updated
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle the exception appropriately in your application
        }
    }
}
