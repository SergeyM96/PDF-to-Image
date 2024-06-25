package com.pdfConvert.util.gui;

import com.pdfConvert.util.Constants;
import com.pdfConvert.util.Utility;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.util.HashMap;

public class HelperMethods {

    public static void ExitKeyCodeCombination(Scene scene, Stage stage) {
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        hashMap.put(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN), Utility::exitProgramAction);
        scene.getAccelerators().putAll(hashMap);
    }

    public static void SetAppDecoration(Stage stage) {
        stage.setTitle(Constants.APP_NAME);
        SetIcon(stage);
    }

    public static void SetIcon(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(new Image("/com/pdfConvert/images/Logo_x1.png"));
    }



}
