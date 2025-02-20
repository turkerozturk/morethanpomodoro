package org.example.astronomy;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.shredzone.commons.suncalc.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

public class MoonPhasesPanel extends JPanel {

    private final LanguageManager bundle = LanguageManager.getInstance();
    private static final ConfigManager props = ConfigManager.getInstance();

    private int year;
    private int month; // 0 = January, 1 = February, ... 11 = December


    private JPanel phasesPanel; // Grid'i içeren panel
    private JPanel navigationPanel; // Butonların olduğu panel

    public MoonPhasesPanel(int year, int month) {
        this.year = year;
        this.month = month;


        initUI();
        fillPhasesForCurrentMonth();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Ay fazları gridini koyacağımız panel
        phasesPanel = new JPanel();
        phasesPanel.setLayout(new GridLayout(0, 2, 10, 5));
        // 0 satır -> satır sayısı dinamik artsın, 2 sütun, hgap=10, vgap=5

        // Önceki / Sonraki ay butonları için panel
        navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("Önceki");
        JButton nextButton = new JButton("Sonraki");

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToPreviousMonth();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToNextMonth();
            }
        });

        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);

        add(navigationPanel, BorderLayout.NORTH);
        add(phasesPanel, BorderLayout.CENTER);
    }

    /**
     * İçinde bulunduğumuz year-month için tüm moon phase’leri hesaplayıp
     * tablo (grid) şeklinde gösterir.
     */
    private void fillPhasesForCurrentMonth() {
        // phasesPanel’i temizleyip yeniden dolduruyoruz
        phasesPanel.removeAll();

        // Bu ayın ilk gününü ve sonraki ayın ilk gününü bulalım
        Calendar startCal = Calendar.getInstance();
        startCal.clear();
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, month);
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.MONTH, 1); // bir sonraki ayın 1.günü

        // Elimizde 8 temel faz var (0°, 45°, 90°, 135°, 180°, 225°, 270°, 315°)
        // suncalc içindeki sabitleri de kullanabiliriz:
        List<MoonPhase.Phase> phaseList = Arrays.asList(
                MoonPhase.Phase.NEW_MOON,
                MoonPhase.Phase.WAXING_CRESCENT,
                MoonPhase.Phase.FIRST_QUARTER,
                MoonPhase.Phase.WAXING_GIBBOUS,
                MoonPhase.Phase.FULL_MOON,
                MoonPhase.Phase.WANING_GIBBOUS,
                MoonPhase.Phase.LAST_QUARTER,
                MoonPhase.Phase.WANING_CRESCENT
        );

        // startCal’dan başlayarak, endCal’a (yani bu ayın sonunu) geçmeyecek şekilde
        // "bir sonraki faz" yöntemini kullanarak tüm faz günlerini buluruz.
        Calendar currentCal = (Calendar) startCal.clone();

        // Basitçe: Her seferinde 8 fazı da compute edip, en erken gerçekleşen tarihi bulmak,
        // sonra oraya gidip +1 gün ekleyip aynı döngüyü sürdürmek şeklinde ilerleyebiliriz.
        // Alternatif olarak, "her gün" içindeki faz aralığını kontrol eden daha karmaşık bir
        // yöntem de var. Burada basit bir "bir sonraki faz" yaklaşımı gösteriyorum.

        while (currentCal.before(endCal)) {
            // O günden itibaren 8 faz için "bir sonraki" zamanı hesaplayıp en yakın olanı bulalım.
            Date earliestPhaseDate = null;
            MoonPhase.Phase earliestPhaseType = null;

            for (MoonPhase.Phase mp : phaseList) {
                MoonPhase.Parameters parameters = MoonPhase.compute()
                        .phase(mp)
                       // .truncatedTo(TimeResultParameter.Unit.DAYS)
                        .on(currentCal);

                MoonPhase nextPhase = parameters.execute();
                ZonedDateTime zdt = nextPhase.getTime(); // ZonedDateTime döner
                Date phaseTime = Date.from(zdt.toInstant());

                // "nextPhase.getTime()" bu fazın *bir sonraki* gerçekleştiği günü verir.

                // Bu tarih, endCal'dan önce mi? Değilse listede olmasın
                if (!phaseTime.before(endCal.getTime())) {
                    continue;
                }

                // En erkeni bulalım
                if (earliestPhaseDate == null || phaseTime.before(earliestPhaseDate)) {
                    earliestPhaseDate = phaseTime;
                    earliestPhaseType = mp;
                }
            }

            if (earliestPhaseDate == null) {
                // Bu ay içinde faz kalmadı demek
                break;
            }

            // Grid'e ekle (yyyy-MM-dd, phaseText)
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(earliestPhaseDate);
            String phaseTextKey = "moon.phase." + earliestPhaseType.name().toLowerCase();
            // Örnek: "moon.phase.full_moon"
            // name() -> FULL_MOON, toLowerCase() -> full_moon
            // İsterseniz harf düzeni vb. handle edebilirsiniz.

            String phaseText = bundle.getString(phaseTextKey);

            phasesPanel.add(new JLabel(dateStr));
            phasesPanel.add(new JLabel(phaseText));

            // currentCal'ı en erken fazın gününe set edelim ve 1 gün ekleyelim
            currentCal.setTime(earliestPhaseDate);
            currentCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Değişiklikleri Swing'e yansıt
        phasesPanel.revalidate();
        phasesPanel.repaint();
    }

    private void goToPreviousMonth() {
        month--;
        if (month < 0) {
            month = 11;
            year--;
        }
        rebuildForNewMonth();
    }

    private void goToNextMonth() {
        month++;
        if (month > 11) {
            month = 0;
            year++;
        }
        rebuildForNewMonth();
    }

    private void rebuildForNewMonth() {
        fillPhasesForCurrentMonth();
    }
}
