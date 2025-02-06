package org.example;

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
