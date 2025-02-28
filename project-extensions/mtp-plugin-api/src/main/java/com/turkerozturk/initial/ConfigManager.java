/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package com.turkerozturk.initial;

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
