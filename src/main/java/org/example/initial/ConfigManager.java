package org.example.initial;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties props;

    private ConfigManager() {
        props = new Properties();

        // 1) Load default config from resources
        // 1) Load default config from resources
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    props.load(isr);
                }
            } else {
                throw new RuntimeException("config.properties not found in resources!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading default config.properties", e);
        }


        // 2) Check for config.properties in the application folder
        File localConfig = new File(System.getProperty("user.dir"), "config.properties");
        if (localConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(localConfig);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

                Properties localProps = new Properties();
                localProps.load(isr);

                for (String key : localProps.stringPropertyNames()) {
                    props.setProperty(key, localProps.getProperty(key));
                }

            } catch (IOException e) {
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
