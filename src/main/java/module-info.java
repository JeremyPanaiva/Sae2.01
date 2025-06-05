module com.example.sae2_01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.sae2_01 to javafx.fxml;
    exports com.example.sae2_01;
}