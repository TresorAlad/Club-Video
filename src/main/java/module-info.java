module com.videocassette {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.videocassette to javafx.fxml;
    opens com.videocassette.controller to javafx.fxml;
    opens com.videocassette.model to javafx.base;

    exports com.videocassette;
    exports com.videocassette.controller;
    exports com.videocassette.model;
    exports com.videocassette.dao;
    exports com.videocassette.util;
}
