package edu.calpoly.csc;

public class LoginState {
    private String username;
    private String token;
    private boolean loggedIn;

    public LoginState(String username, String token) {
        this.username = username;
        this.token = token;
        this.loggedIn = true;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void logout() {
        username = "";
        token = "";
        loggedIn = false;
    }
}
