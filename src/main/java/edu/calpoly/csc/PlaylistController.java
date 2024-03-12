package edu.calpoly.csc;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PlaylistController implements Initializable {

    private Playlist playlist;
    private int playlistId;

    @FXML private Label plLabel;
    @FXML private TableView<Song> songsView;
    @FXML private TableView<Song> plView;
    @FXML private TableColumn<Song, String> mNameCol;
    @FXML private TableColumn<Song, String> mArtistCol;
    @FXML private TableColumn<Song, String> mAlbumCol;
    @FXML private TableColumn<Song, String> mDurationCol;
    @FXML private TableColumn<Song, String> mGenreCol;
    @FXML private TableColumn<Song, String> sNameCol;
    @FXML private TableColumn<Song, String> sArtistCol;
    @FXML private TableColumn<Song, String> sAlbumCol;
    @FXML private TableColumn<Song, String> sDurationCol;
    @FXML private TableColumn<Song, String> sGenreCol;

    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Button acb;
    @FXML private Button rcb;
    @FXML private Button pubButton;

    @FXML private TextField collabField;
    @FXML private TableView<User> collabView;
    @FXML private TableColumn<User, String> collabCol;

    private Song selectedSong;
    private Song selectedEntry;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Logic to fetch and display playlist information from the database
        playlist = App.getPlaylist();
        playlistId = playlist.getPlID();
        plLabel.setText(playlist.getPlName());

        collabCol.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        mNameCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sTitle"));
        mArtistCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sArtist"));
        mAlbumCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sAlbum"));
        mDurationCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sDuration"));
        mGenreCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sGenre"));
        sNameCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sTitle"));
        sArtistCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sArtist"));
        sAlbumCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sAlbum"));
        sDurationCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sDuration"));
        sGenreCol.setCellValueFactory(new PropertyValueFactory<Song, String>("sGenre"));

        addButton.disableProperty().bind(songsView.getSelectionModel().selectedItemProperty().isNull());
        removeButton.disableProperty().bind(plView.getSelectionModel().selectedItemProperty().isNull());
        acb.disableProperty().bind(collabField.textProperty().isEmpty());
        rcb.disableProperty().bind(collabView.getSelectionModel().selectedItemProperty().isNull());

        plView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected: "+newSelection.getSTitle());
                if (newSelection == selectedSong) {
                    System.out.println("Same as before");
                }
                else {
                    selectedEntry = newSelection;
                    selectedSong = null;
                }
            } else {
                // Switching Tabs
                selectedSong = null;
            }
        });
        
        songsView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected: "+newSelection.getSTitle());
                if (newSelection == selectedSong) {
                    System.out.println("Same as before");
                }
                else {
                    selectedEntry = null;
                    selectedSong = newSelection;
                }
            } else {
                // Switching Tabs
                selectedSong = null;
            }
        });

        refresh();
    }

    private void refresh() {
        boolean isPublic = Database.getInstance().getPlaylistVisibility(playlistId);
        pubButton.setText(isPublic ? "Make Private" : "Make Public");

        List<Song> pl = Database.getInstance().getPlaylistEntries(playlistId);
        List<Song> s = Database.getInstance().getSongs();

        for (Song song : pl) {
            s.removeIf(s2 -> s2.equals(song));
        }

        plView.getItems().setAll(pl);
        songsView.getItems().setAll(s);
        collabView.getItems().setAll(Database.getInstance().viewCollaborators(playlistId));
    }

    @FXML
    private Label statusLabel;

    // Existing methods...

    @FXML
    private void goBack() {
        App.setRoot("user");
    }

    @FXML
    private void handleAddPlaylistEntry() {
        int songID = selectedSong.getSID();

        // Logic to add a playlist entry to the database
        if (Database.getInstance().addPlaylistEntry(playlistId, songID)) {
            statusLabel.setText("Playlist entry added successfully!");
        } else {
            statusLabel.setText("Failed to add playlist entry.");
        }

        refresh();
    }

    @FXML
    private void handleRemovePlaylistEntry() {
        int entryId = selectedEntry.getSID();
        // Logic to remove a playlist entry from the database
        if (Database.getInstance().removePlaylistEntry(playlistId, entryId)) {
            statusLabel.setText("Playlist entry removed successfully!");
        } else {
            statusLabel.setText("Failed to remove playlist entry.");
        }

        refresh();
    }

    @FXML
    private void handleTogglePublic() {
        // Logic to toggle playlist public/private
        boolean isPublic = Database.getInstance().getPlaylistVisibility(playlistId);
        if (Database.getInstance().togglePlaylistVisibility(playlistId, !isPublic)) {
            statusLabel.setText("Playlist visibility toggled successfully!");
            pubButton.setText(!isPublic ? "Make Private" : "Make Public");
        } else {
            statusLabel.setText("Failed to toggle playlist visibility.");
        }

        refresh();
    }

    @FXML
    private void handleAddCollaborator() {
        String collaboratorUsername = collabField.getText();

        // Logic to add a collaborator to the playlist
        if (Database.getInstance().addCollaborator(playlistId, collaboratorUsername)) {
            statusLabel.setText("Collaborator added successfully!");
        } else {
            statusLabel.setText("Failed to add collaborator.");
        }

        refresh();
    }

    @FXML
    private void handleRemoveCollaborator() {
        int collaboratorId = 1 /* Get the collaborator ID */;

        // Logic to remove a collaborator from the playlist
        if (Database.getInstance().removeCollaborator(playlistId, collaboratorId)) {
            statusLabel.setText("Collaborator removed successfully!");
        } else {
            statusLabel.setText("Failed to remove collaborator.");
        }

        refresh();
    }

}
