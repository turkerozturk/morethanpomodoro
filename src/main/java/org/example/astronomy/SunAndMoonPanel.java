package org.example.astronomy;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;

public class SunAndMoonPanel extends JPanel {

    private static String timezone;

    private static double longitude;


    private static double latitude;


    CommonsSunCalc data;


    private final LanguageManager bundle = LanguageManager.getInstance();
    private static final ConfigManager props = ConfigManager.getInstance();

    public SunAndMoonPanel() {



        timezone = props.getProperty("location.timezone");
        latitude=Double.parseDouble(props.getProperty("location.latitude"));
        longitude = Double.parseDouble(props.getProperty("location.longitude"));

        //double lat = 41.01035651785811;
        //double longitude = 28.973887666765542;

        data = new CommonsSunCalc(
                timezone,
                latitude,
                longitude
        );

        data.getAstronomy();



        // Layout ayarı
        setLayout(new BorderLayout());

        // TabbedPane oluştur
        JTabbedPane tabbedPane = new JTabbedPane();

        // Sekmeleri ekle
        tabbedPane.addTab(bundle.getString("suncalc.tab.main"), createMainTab());
        tabbedPane.addTab(bundle.getString("suncalc.tab.sun"), createSunDetailsTab());
        //tabbedPane.addTab(bundle.getString("suncalc.tab.sun"), createSunDetailsTab2());
        tabbedPane.addTab(bundle.getString("suncalc.tab.sun.rises"), createSunRiseDetailsTab());
        tabbedPane.addTab(bundle.getString("suncalc.tab.sun.sets"), createSetDetailsTab());
        tabbedPane.addTab(bundle.getString("suncalc.tab.moon"), createMoonDetailsTab());

        // TODO moon phases

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 1. Sekme (Main): Güneş & Ay temel zamanları
     */
    private JPanel createMainTab() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Güneş doğuşu
        panel.add(new JLabel(bundle.getString("suncalc.sunrise")));
        panel.add(new JLabel(nullSafe(data.getSunRiseFormatted())));

        // Güneş batışı
        panel.add(new JLabel(bundle.getString("suncalc.sunset")));
        panel.add(new JLabel(nullSafe(data.getSunSetFormatted())));

        // Ay doğuşu
        panel.add(new JLabel(bundle.getString("suncalc.moonrise")));
        panel.add(new JLabel(nullSafe(data.getMoonRiseFormatted())));

        // Ay batışı
        panel.add(new JLabel(bundle.getString("suncalc.moonset")));
        panel.add(new JLabel(nullSafe(data.getMoonSetFormatted())));

        return panel;
    }

    /**
     * 2. Sekme (Sun Details): Güneşle ilgili diğer değişkenler
     */
    private JPanel createSunDetailsTab() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Örnek olarak eklediğimiz tüm metotları ekleyelim:
        panel.add(new JLabel(bundle.getString("suncalc.sunNadir")));
        panel.add(new JLabel(nullSafe(data.getSunNadirFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.sunAzimuth")));
        panel.add(new JLabel(nullSafe(data.getSunAzimuthFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.sunAltitude")));
        panel.add(new JLabel(nullSafe(data.getSunAltitudeFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.sunDistance")));
        panel.add(new JLabel(nullSafe(data.getSunDistanceFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.sunNoon")));
        panel.add(new JLabel(nullSafe(data.getSunNoonFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.dayLength")));
        panel.add(new JLabel(nullSafe(data.getDayLength())));

        String yes = bundle.getString("word.yes");
        String no = bundle.getString("word.no");

        //panel.add(new JLabel(bundle.getString("suncalc.isAlwaysUp")));
       // TODO check: panel.add(new JLabel(nullSafe(data.isSunAlwaysUp() ? yes : no)));

       // panel.add(new JLabel(bundle.getString("suncalc.isAlwaysDown")));
       // TODO check panel.add(new JLabel(nullSafe(data.isSunAlwaysDown() ? yes : no)));

        return panel;
    }


    private JPanel createSunRiseDetailsTab() {

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        // Güneşin ayrıntılı zamanları (astronomical, civil, goldenHour vs.)
        panel.add(new JLabel(bundle.getString("suncalc.sunRiseAstronomical")));
        panel.add(new JLabel(nullSafe(data.getSunRiseAstronomical())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseNautical")));
        panel.add(new JLabel(nullSafe(data.getSunRiseNautical())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseNightHour")));
        panel.add(new JLabel(nullSafe(data.getSunRiseNightHour())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseCivil")));
        panel.add(new JLabel(nullSafe(data.getSunRiseCivil())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseBlueHour")));
        panel.add(new JLabel(nullSafe(data.getSunRiseBlueHour())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseVisual")));
        panel.add(new JLabel(nullSafe(data.getSunRiseVisual())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseVisualLower")));
        panel.add(new JLabel(nullSafe(data.getSunRiseVisualLower())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseHorizon")));
        panel.add(new JLabel(nullSafe(data.getSunRiseHorizon())));

        panel.add(new JLabel(bundle.getString("suncalc.sunRiseGoldenHour")));
        panel.add(new JLabel(nullSafe(data.getSunRiseGoldenHour())));

        return panel;
    }

    private JPanel createSetDetailsTab() {

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetGoldenHour")));
        panel.add(new JLabel(nullSafe(data.getSunSetGoldenHour())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetHorizon")));
        panel.add(new JLabel(nullSafe(data.getSunSetHorizon())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetVisualLower")));
        panel.add(new JLabel(nullSafe(data.getSunSetVisualLower())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetVisual")));
        panel.add(new JLabel(nullSafe(data.getSunSetVisual())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetBlueHour")));
        panel.add(new JLabel(nullSafe(data.getSunSetBlueHour())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetCivil")));
        panel.add(new JLabel(nullSafe(data.getSunSetCivil())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetNightHour")));
        panel.add(new JLabel(nullSafe(data.getSunSetNightHour())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetNautical")));
        panel.add(new JLabel(nullSafe(data.getSunSetNautical())));

        panel.add(new JLabel(bundle.getString("suncalc.sunSetAstronomical")));
        panel.add(new JLabel(nullSafe(data.getSunSetAstronomical())));


        return panel;
    }

    /**
         * 3. Sekme (Moon Details): Ayla ilgili diğer değişkenler
         */
    private JPanel createMoonDetailsTab() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        panel.add(new JLabel(bundle.getString("suncalc.moonAzimuth")));
        panel.add(new JLabel(nullSafe(data.getMoonAzimuthFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.moonAltitude")));
        panel.add(new JLabel(nullSafe(data.getMoonAltitudeFormatted())));

        panel.add(new JLabel(bundle.getString("suncalc.moonDistance")));
        panel.add(new JLabel(nullSafe(data.getMoonDistanceFormatted())));

        // Eğer elinizde fazladan ay parametreleri (ör. faz, aydınlanma, vb.) varsa burada ekleyin.

        return panel;
    }

    /**
     * null değer gelirse boş string döndürür, aksi halde değeri döndürür.
     */
    private String nullSafe(String value) {
        return (value == null) ? "" : value;
    }
}
