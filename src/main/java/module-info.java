module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.demo.controller to javafx.fxml;
    exports org.example.demo.app;
}
