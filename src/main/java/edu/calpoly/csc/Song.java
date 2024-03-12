package edu.calpoly.csc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Song {
    SimpleIntegerProperty sID;
    SimpleStringProperty sTitle;
    SimpleStringProperty sArtist;
    SimpleStringProperty sAlbum;
    SimpleStringProperty sDuration;
    SimpleStringProperty sGenre;

    public Song(int id, String title, String artist, String author, String Duration, String genre) {
        sID = new SimpleIntegerProperty(id);
        sTitle = new SimpleStringProperty(title);
        sArtist = new SimpleStringProperty(artist);
        sAlbum = new SimpleStringProperty(author);
        sDuration = new SimpleStringProperty(Duration);
        sGenre = new SimpleStringProperty(genre);
    }
    
    public String getSTitle() {
        return sTitle.get();
    }

    public String getSArtist() {
        return sArtist.get();
    }

    public String getSAlbum() {
        return sAlbum.get();
    }

    public String getSDuration() {
        return sDuration.get();
    }

    public String getSGenre() {
        return sGenre.get();
    }

    public int getSID() {
        return sID.get();
    }

    public boolean equals(Song s) {
        return s.getSID() == this.getSID();
    }
}
