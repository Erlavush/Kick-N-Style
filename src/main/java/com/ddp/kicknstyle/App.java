package com.ddp.kicknstyle;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/mainGUI.fxml"));
        
     
        Parent root = loader.load();
        
      
        Rectangle clip = new Rectangle(1200, 800); 
        clip.setArcWidth(110); 
        clip.setArcHeight(110); 
        root.setClip(clip);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); 
        
        
        scene.getStylesheets().add(getClass().getResource("/com/ddp/KickNStyle/mainCSS.css").toExternalForm());
        
       
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("test");
        launch(args);
        
    }
}
