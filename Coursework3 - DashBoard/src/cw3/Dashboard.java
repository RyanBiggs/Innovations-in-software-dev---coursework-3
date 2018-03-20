package cw3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application
{    
    //*****************************************
    //  start - Called when application starts
    //*****************************************
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        
        Scene scene = new Scene(root);
        // scene.getStylesheets().add(cw3.class.getResource("Style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("BI dashboard");
        primaryStage.show();
    }

    //*********************
    //  main - main method
    //*********************
    public static void main(String[] args)
    {
        launch(args);
    }    
}