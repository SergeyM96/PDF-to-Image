package com.pdfConvert.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Constants {
    public enum Mode {PRODUCTION, DEVELOPMENT}


    public static String assetsPath;

    public final static String APP_NAME = "PDF to Image Convertor";

    public final static Mode RUNNING_MODE = Mode.DEVELOPMENT;

    static {
        try {
            if (Files.isWritable(Paths.get(Constants.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                assetsPath = "jarFiles";
            } else {
                assetsPath = System.getenv("LOCALAPPDATA") + "/jarFiles";
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }


}
