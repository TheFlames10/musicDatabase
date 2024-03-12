package edu.calpoly.csc;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Playlist {
    SimpleIntegerProperty plID;
    SimpleStringProperty plName;
    SimpleStringProperty plCreator;
    SimpleBooleanProperty plPublic;
    SimpleStringProperty plLink;
    SimpleBooleanProperty shared;

    public Playlist(int id, String name, String creator, boolean isPublic, boolean shared) {
        plCreator = new SimpleStringProperty(creator);
        plName = new SimpleStringProperty(name);
        plID = new SimpleIntegerProperty(id);
        plPublic = new SimpleBooleanProperty(isPublic);
        this.shared = new SimpleBooleanProperty(shared);
        // plLink = new SimpleStringProperty(link);
    }

    public String getPlName() {
        return plName.get();
    }

    public void setPlName(String plName) {
        this.plName.set(plName);
    }

    public String getPlCreator() {
        return plCreator.get();
    }

    public void setPlCreator(String plCreator) {
        this.plCreator.set(plCreator);
    }

    public boolean isPlPublic() {
        return plPublic.get();
    }

    public void setPlPublic(boolean plPublic) {
        this.plPublic.set(plPublic);
    }

    public boolean isShared() {
        return shared.get();
    }

    public int getPlID() {
        return plID.get();
    }

    public void setPlID(int plID) {
        this.plID.set(plID);
    }
}
