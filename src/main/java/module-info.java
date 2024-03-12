module edu.calpoly.csc {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.calpoly.csc to javafx.fxml;
    exports edu.calpoly.csc;
}
