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
package com.turkerozturk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void appendToHistory(String text) {
        File file = new File("history.txt");

        try (FileWriter writer = new FileWriter(file, true)) { // Append modunda açıyoruz
            writer.write(text + System.lineSeparator()); // Satır olarak ekliyoruz
        } catch (IOException e) {
            System.err.println("Dosyaya yazılırken hata oluştu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        appendToHistory("Bu bir test satırıdır.");
    }
}
