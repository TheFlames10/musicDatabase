package edu.calpoly.csc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static LoginState loginState;
    // private static int playlist = -1;
    private static Playlist playlist = null;

    @Override
    public void start(Stage stage) throws IOException {
        loginState = null; //new LoginState("aidan", "token");
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setPlaylist(Playlist plID) {
        playlist = plID;
        setRoot("Playlist");
    }

    static Playlist getPlaylist() {
        return playlist;
    }

    static void setLoginState(String username, String token) {
        loginState = new LoginState(username, token);
    }

    static LoginState getLoginState() {
        return loginState;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}