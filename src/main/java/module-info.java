module org.example.gimnasioproyect {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires org.example.gimnasioproyect;
    requires static lombok;
    requires javafx.graphics;
    requires telegrambots.meta;
    requires telegrambots;

    //requires org.example.gimnasioproyect;
    //requires org.example.gimnasioproyect;
    //requires org.example.gimnasioproyect;


    opens org.example.gimnasioproyect to javafx.fxml;
    exports org.example.gimnasioproyect;
    exports org.example.gimnasioproyect.model;
    opens org.example.gimnasioproyect.model to javafx.fxml;
    exports org.example.gimnasioproyect.controllers;
    opens org.example.gimnasioproyect.controllers to javafx.fxml;
}