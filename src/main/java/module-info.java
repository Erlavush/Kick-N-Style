module com.ddp.kicknstyle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires javafx.base;
    
    
    opens com.ddp.kicknstyle.controller to javafx.fxml;
    opens com.ddp.kicknstyle to javafx.fxml;
    exports com.ddp.kicknstyle;
}
