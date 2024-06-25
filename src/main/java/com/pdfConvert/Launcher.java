package com.pdfConvert;

import com.pdfConvert.util.Constants;
import com.pdfConvert.util.Logger;
import com.pdfConvert.util.Utility;
import com.pdfConvert.util.gui.HelperMethods;
import com.pdfConvert.util.gui.load.Locations;
import com.pdfConvert.util.validation.SingleInstance;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static Long startTime;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        launch(args);
    }

    @Override
    public void stop() {
        System.out.println("stop()...");
        Utility.exitProgramAction();
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();

        // create folder if not existing
        Utility.createDirectory(Constants.assetsPath + "/logs");

        // initialize logger
        Logger.init();
        Logger.info("App Launched");
    }

    public void start(Stage primaryStage) {
        try {
            //  load Homepage FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Locations.Home.toString()));
            Scene scene = new Scene(loader.load());
            // add loaded scene to primaryStage
            primaryStage.setScene(scene);
            // set Title and Icon to primaryStage
            HelperMethods.SetAppDecoration(primaryStage);
            HelperMethods.ExitKeyCodeCombination(scene, primaryStage);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> Utility.exitProgramAction());
            // assign current primaryStage to SingleInstance Class
            SingleInstance.getInstance().setCurrentStage(primaryStage);
        } catch (Exception ex) {
            Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".start()");
        }
    }
}
