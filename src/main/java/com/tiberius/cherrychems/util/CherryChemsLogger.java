package com.tiberius.cherrychems.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author tiberius
 */
public class CherryChemsLogger {

    static private Logger logger = Logger.getLogger("CherryChems");

    public static void init() {
        try {
            FileHandler fh;
            fh = new FileHandler(System.getProperty("user.dir") + "/cherrychems.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception: " + e.getMessage());
        }
    }

    private static void logDebug(String text) {
        logger.log(Level.INFO, text);
    }

    private static void logWarning(String text) {
        logger.log(Level.WARNING, text);
    }

    private static void logCritical(String text) {
        logger.log(Level.SEVERE, text);
    }

    public static void log(String type, String text) {
        try {
            switch (type) {
                case "D":
                case "debug":
                    logDebug(text);
                    break;
                case "W":
                case "warning":
                    logWarning(text);
                    break;
                case "C":
                case "critical":
                    logCritical(text);
                    break;
            }

        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Security Exception: " + e.getMessage());
        }

    }

}
