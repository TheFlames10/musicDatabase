package edu.calpoly.csc;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private SimpleStringProperty name;

    public User(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return name.get();
    }
}