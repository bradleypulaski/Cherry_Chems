package com.tiberius.cherrychems.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author tiberius
 */
public class PropertyProvider {

    private static HashMap properties = new HashMap();
    private static long fileLastModified = 1;

    public PropertyProvider() {
        PropertyProvider.init();
    }

    public static void init() {

        properties.clear();

        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + "/cherrychems.properties")) {

            Properties prop = new Properties();

            prop.load(input);

            Enumeration e = prop.propertyNames();

            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                properties.put(key, value);
            }

            CherryChemsLogger.log("debug", "properties reset....");

        } catch (IOException e) {
            CherryChemsLogger.log("critical", e.getMessage());
        }
    }

    private static void fileModified() { 
        File propertyFile = new File(System.getProperty("user.dir") + "/cherrychems.properties");
        long lastModified = propertyFile.lastModified();
        if (fileLastModified != lastModified) {
            init();
            fileLastModified = lastModified;
        }
    }

    // Getters
    public static HashMap getProperties() {
        fileModified();
        return properties;
    }

    public static String getProperty(String key) {
        fileModified();
        return (String) properties.get(key);
    }

}
