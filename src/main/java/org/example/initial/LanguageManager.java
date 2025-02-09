package org.example.initial;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle bundle;
    private Locale locale;

    private LanguageManager() {
        ConfigManager configManager = ConfigManager.getInstance();

        // config.properties dosyasından dil ve ülke bilgilerini al
        String language = configManager.getProperty("language.locale", "en");
        String country = configManager.getProperty("language.country", "US");

        // Locale ve ResourceBundle oluştur
        locale = new Locale(language, country);
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public Locale getLocale() {
        return locale;
    }

    // Eğer dinamik dil değiştirme istiyorsan, şu metodu kullanabilirsin:
    public void setLocale(String language, String country) {
        locale = new Locale(language, country);
        bundle = ResourceBundle.getBundle("messages", locale);
    }
}
