package edu.calpoly.csc;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.UUID;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleTrueLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Perform login authentication logic
        if (Database.getInstance().isValidCredentials(username, password.trim())) {
            statusLabel.setText("Login successful!");
            App.setLoginState(username.trim(), password);
            System.out.println(username);
            App.setRoot("user");
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

    
        if (Database.getInstance().isUsernameAvailable(username)) {
            // Perform registration logic
            if (Database.getInstance().registerUser(username, password, token)) {
                statusLabel.setText("Account registered!");
            } else {
                statusLabel.setText("Error registering account.");
            }
        } else {
            statusLabel.setText("Username is already taken. Please choose another.");
        }
    }
}
