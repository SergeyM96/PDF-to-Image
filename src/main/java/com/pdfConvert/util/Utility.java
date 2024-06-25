package com.pdfConvert.util;

import com.pdfConvert.Launcher;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Helper methods
 */
public class Utility {


    // exiting the program
    public static void exitProgramAction() {
//        printAllRunningThreads();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Long exitTime = System.currentTimeMillis();
            Logger.info("App closed - Used for "
                    + (exitTime - Launcher.startTime) + " ms\n");
        }));
        System.exit(0);
    }

// path where the directory needs to be created
    public static void createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static double formatNum(double num) {
        double returnedVal = Double.parseDouble(String.format("%.2f", num));
        return Math.abs(returnedVal) == 0 ? 0 : returnedVal;
    }

}
