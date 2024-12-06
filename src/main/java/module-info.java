module com.ddp.kicknstyle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    
    opens com.ddp.kicknstyle to javafx.fxml;
    exports com.ddp.kicknstyle;
}
