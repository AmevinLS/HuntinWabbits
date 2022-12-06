module com.example.huntinwabbits {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.huntinwabbits to javafx.fxml;
    exports com.example.huntinwabbits;
}