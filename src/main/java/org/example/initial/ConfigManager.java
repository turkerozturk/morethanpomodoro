package org.example.initial;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private Properties props;

    private ConfigManager() {
        props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                throw new RuntimeException("config.properties dosyası bulunamadı!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Config properties yüklenirken hata oluştu", e);
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
}
