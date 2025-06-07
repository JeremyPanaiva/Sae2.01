module bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.bomberman to javafx.fxml;
    opens com.bomberman.controller to javafx.fxml;

    exports com.bomberman;
}