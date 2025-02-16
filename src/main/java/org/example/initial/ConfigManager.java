package org.example.initial;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties props;

    private ConfigManager() {
        props = new Properties();

        // 1) Load default config from resources
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                throw new RuntimeException("config.properties not found in resources!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading default config.properties", e);
        }

        // 2) Check for config.properties in the application folder
        File localConfig = new File(System.getProperty("user.dir"), "config.properties");
        if (localConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(localConfig)) {
                // 3) If exists, load it into localProps
                Properties localProps = new Properties();
                localProps.load(fis);

                // 4) Overwrite matching keys in props
                for (String key : localProps.stringPropertyNames()) {
                    props.setProperty(key, localProps.getProperty(key));
                }
            } catch (IOException e) {
                // You could choose to ignore or handle the exception
                e.printStackTrace();
            }
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

}
