package edu.calpoly.csc;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserController implements Initializable {

    @FXML private TableView<Playlist> tableView;
    @FXML private TableColumn<Playlist, String> plNameCol;
    @FXML private TableColumn<Playlist, String> plCreatorCol;
    @FXML private TableColumn<Playlist, String> plPublicCol;

    @FXML private TextField plNameField;
    @FXML private CheckBox plPublicField;

    @FXML private TextField nameField;

    @FXML private Button primaryButton;
    @FXML private Button secondaryButton;
    @FXML private Button deleteButton;
    @FXML private Button createButton;

    private boolean isPrimary = true;

    private Playlist selectedPlaylist;
    private String draftName = "";
    private boolean editing = false;

    private List<Playlist> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateButtons();
        plNameCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("plName"));
        plCreatorCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("plCreator"));
        plPublicCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("plPublic"));

        createButton.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected: "+newSelection.getPlName());
                if (newSelection == selectedPlaylist && editing) {
                    System.out.println("Same as before");
                }
                else {
                    selectedPlaylist = newSelection;
                    draftName = selectedPlaylist.getPlName();
                    editing = true;
                    nameField.setText(selectedPlaylist.getPlName());
                }
            } else {
                // Switching Tabs
                selectedPlaylist = null;
                draftName = "";
                editing = false;
                nameField.setText("");
            }
            updateSettings();
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                draftName = "";
                selectedPlaylist = null;
                editing = false;
                System.out.println("Resetting");
            } else {
                draftName = newValue;
            }

            updateSettings();
        });

        tableView.setRowFactory(tv -> {
            TableRow<Playlist> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Playlist rowData = row.getItem();
                    System.out.println("Double click on: "+rowData.getPlName());
                    App.setPlaylist(rowData);
                }
            });
            return row;
        });

        tableView.getItems().setAll(parseUserList());
    }

    private void updateSettings() {
        // Set Create Button Disable
        // Set Create Button Text
        if (draftName.equals("")) { // No name provided
            createButton.setDisable(true);
            deleteButton.setDisable(true);
            if (isPrimary)
                createButton.setText("Create");
        } else {
            if (!editing) { // Making new playlist
                createButton.setDisable(false);
                deleteButton.setDisable(true);
                if (isPrimary)
                    createButton.setText("Create");
            } else { // Editing existing playlist
                createButton.setText("Rename");
                deleteButton.setDisable(false);
                if (selectedPlaylist != null)
                    createButton.setDisable(draftName.equals(selectedPlaylist.getPlName()));
                else
                    createButton.setDisable(false);
            }
        }
    }

    private void refresh() {
        tableView.getItems().setAll(parseUserList());
    }

    private void fetch(String username) {
        if (data == null) {
            data = new ArrayList<Playlist>();
        } 
        else data.clear();
        data.addAll(Database.getInstance().getPlaylists(username));
    }

    private List<Playlist> parseUserList(){
        String currentUser = App.getLoginState().getUsername();
        fetch(currentUser);
        List<Playlist> list = new ArrayList<>(data);
        if (isPrimary)
            list.removeIf(p -> (p.isShared()));
        else
            list.removeIf(p -> (!p.isShared()));
        
        return list;
    }

    @FXML
    private void switchToPrimary() throws IOException {
        // App.getLoginState().logout();
        App.setRoot("login");
    }

    @FXML
    private void primaryButton() throws IOException {
        isPrimary = true;
        createButton.setText("Create");
        updateButtons();
        tableView.getItems().setAll(parseUserList());
    }

    @FXML
    private void secondaryButton() throws IOException {
        isPrimary = false;
        updateButtons();
        tableView.getItems().setAll(parseUserList());
    }

    private void updateButtons() {
        updateSettings();

        deleteButton.setVisible(isPrimary);
        // primaryButton.setDisable(isPrimary);
        // secondaryButton.setDisable(!isPrimary);
        primaryButton.setStyle(
            "-fx-background-radius: 8px 0 0 8px;" + 
            (isPrimary ? "-fx-background-color: #31B6F3;" : ""));
        secondaryButton.setStyle(
            "-fx-background-radius: 0 8px 8px 0;" + 
            (isPrimary ? "" : "-fx-background-color: #31B6F3;"));
        plCreatorCol.setVisible(!isPrimary);
        plPublicCol.setVisible(isPrimary);
    }

    @FXML
    private void onCreate() {
        if (editing) {
            System.out.println("updating (" + selectedPlaylist.getPlID() + ", " + draftName + ")");
            Database.getInstance().renamePlaylist(selectedPlaylist.getPlID(), draftName);
        } else {
            draftName = nameField.getText();
            System.out.println("creating (" + draftName + ")");
            Database.getInstance().createPlaylist(App.getLoginState().getUsername(), draftName, false);
            nameField.setText("");
        }
        refresh();
    }

    @FXML
    private void onDelete() {
        int id = selectedPlaylist.getPlID();
        System.out.println("delete " + id);
        Database.getInstance().deletePlaylist(id);
        refresh();
    }
}
