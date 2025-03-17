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
package com.turkerozturk.playlist;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public static void openFileLocation(File file) {
        if (!file.exists()) {
            System.out.println("Dosya mevcut değil: " + file.getAbsolutePath());
            return;
        }

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Windows için Explorer'da göster
                Runtime.getRuntime().exec("explorer /select,\"" + file.getAbsolutePath() + "\"");
            } else if (os.contains("mac")) {
                // macOS için Finder'da göster
                Runtime.getRuntime().exec(new String[]{"open", "-R", file.getAbsolutePath()});
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                // Linux/Unix için uygun dosya yöneticisini belirle
                String fileManager = detectLinuxFileManager();
                if (fileManager != null) {
                    new ProcessBuilder(fileManager, file.getParent()).start();
                } else {
                    System.out.println("Desteklenen bir dosya yöneticisi bulunamadı, terminalde açılıyor...");
                    new ProcessBuilder("xdg-open", file.getParent()).start();
                }
            } else {
                System.out.println("Bilinmeyen işletim sistemi, dosya açma işlemi desteklenmiyor.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String detectLinuxFileManager() {
        String[] possibleFileManagers = {"xdg-open", "nautilus", "dolphin", "thunar", "pcmanfm", "nemo", "caja"};
        for (String fm : possibleFileManagers) {
            if (isCommandAvailable(fm)) {
                return fm;
            }
        }
        return null;
    }

    private static boolean isCommandAvailable(String command) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", command});
            return process.getInputStream().read() != -1;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        File file = new File("/home/kullanici/Downloads/test.mp3"); // Örnek dosya yolu (Linux)
        openFileLocation(file);
    }
}

