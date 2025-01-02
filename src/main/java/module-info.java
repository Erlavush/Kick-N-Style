module com.ddp.kicknstyle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires javafx.base;
    requires fontawesomefx;
    requires org.kordamp.ikonli.core;         // Contains the Ikon interface
    requires org.kordamp.ikonli.javafx;       // JavaFX-based FontIcon, etc.
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.graphics; 
    
    
    opens com.ddp.kicknstyle.controller to javafx.fxml;
    opens com.ddp.kicknstyle to javafx.fxml;
    opens com.ddp.kicknstyle.model to javafx.base;
    exports com.ddp.kicknstyle;
}
