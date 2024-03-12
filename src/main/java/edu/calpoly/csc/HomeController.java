package edu.calpoly.csc;

import java.io.IOException;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    private void handleLogin() throws IOException {
        // Add logic for handling login here
        App.setRoot("Login");
        System.out.println("Login button clicked!");
    }

    @FXML
    private void handleUser() throws IOException {
        // Add logic for handling register here
        App.setRoot("user");
    }
}
