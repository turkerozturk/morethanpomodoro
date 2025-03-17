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

import javax.swing.*;
import java.io.File;

public class FileChooserUtil {

    /**
     * Kullanıcıdan bir .m3u8 playlist dosyası seçmesini ister ve seçilen dosyanın tam yolunu döndürür.
     */
    public static String selectM3U8File() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select M3U8 Playlist File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Sadece .m3u8 uzantılı dosyaları filtrele
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("M3U8 Playlist Files (*.m3u8)", "m3u8"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null; // Kullanıcı seçim yapmazsa null döndür
        }
    }

    public static void main(String[] args) {
        String path = selectM3U8File();
        if (path != null) {
            System.out.println("Seçilen M3U8 Dosyası: " + path);
        } else {
            System.out.println("Dosya seçimi iptal edildi.");
        }
    }
}
