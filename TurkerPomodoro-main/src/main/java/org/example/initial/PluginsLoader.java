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
package org.example.initial;

import org.example.PanelPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PluginsLoader {

    public static ServiceLoader<PanelPlugin> loadPanelPlugins() {

        File extDir = new File("extensions");
        File[] jarFiles = extDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return null;
        }

// 1) Tüm eklenti jarlarının URL'lerini array'e atıyoruz
        List<URL> urls = new ArrayList<>();
        for (File jar : jarFiles) {
            try {
                urls.add(jar.toURI().toURL());
             //   System.out.println("PluginsLoader.java: " + jar.getName());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

// 2) Kendimize özel bir ClassLoader oluşturuyoruz
        URLClassLoader extensionClassLoader = new URLClassLoader(
                urls.toArray(new URL[0]),
                PluginsLoader.class.getClassLoader() // parent
        );

// 3) ServiceLoader kullanarak PanelPlugin arayüzünü implemente edenleri bul
        ServiceLoader<PanelPlugin> loader = ServiceLoader.load(PanelPlugin.class, extensionClassLoader);


        /* for debug
        for (PanelPlugin plugin : loader) {
            System.out.println("Plugin Class: " + plugin.getClass().getName());
            System.out.println("Plugin Details: " + plugin.toString()); // toString() metodunu override edebilirsin.
        }

         */

        return loader;

    }

}
