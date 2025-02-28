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
package com.turkerozturk.jpanels.theme;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;
import com.turkerozturk.ApplicationFrame;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;

public class ThemeSelectorPanel extends JPanel {


    private String themeName;
    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    public ThemeSelectorPanel(ApplicationFrame parentFrame) {

        loadFromConfig();

        JLabel label = new JLabel(bundle.getString("settings.select.theme"));
        JComboBox<String> themeSelector = new JComboBox<>(themes);
        themeSelector.setSelectedItem(themeName);

        // Seçenekler Arayüze Ekleniyor
        add(label);
        add(themeSelector);

        String fullClassName = UIManager.getLookAndFeel().getClass().getCanonicalName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        themeSelector.setSelectedItem(simpleClassName);

        // Buton Tıklanınca Seçili Temayı Uygula
        themeSelector.addActionListener(e -> applyTheme((String) themeSelector.getSelectedItem(), parentFrame));
    }

    private void loadFromConfig() {
        themeName = props.getProperty("gui.theme.template.name");
    }




    private static final String[] themes = {
            "FlatLightLaf", "FlatDarkLaf", "FlatIntelliJLaf", "FlatDarculaLaf",
            "Swing (Metal)",
            "FlatArcIJTheme", "FlatArcOrangeIJTheme", "FlatArcDarkIJTheme", "FlatArcDarkOrangeIJTheme",
            "FlatCarbonIJTheme", "FlatCobalt2IJTheme", "FlatCyanLightIJTheme", "FlatDarkFlatIJTheme",
            "FlatDarkPurpleIJTheme", "FlatDraculaIJTheme", "FlatGradiantoDarkFuchsiaIJTheme",
            "FlatGradiantoDeepOceanIJTheme", "FlatGradiantoMidnightBlueIJTheme",
            "FlatGradiantoNatureGreenIJTheme", "FlatGrayIJTheme", "FlatGruvboxDarkHardIJTheme",
            "FlatGruvboxDarkMediumIJTheme", "FlatGruvboxDarkSoftIJTheme", "FlatHiberbeeDarkIJTheme",
            "FlatHighContrastIJTheme", "FlatLightFlatIJTheme", "FlatMaterialDesignDarkIJTheme",
            "FlatMonocaiIJTheme", "FlatMonokaiProIJTheme", "FlatNordIJTheme", "FlatOneDarkIJTheme",
            "FlatSolarizedDarkIJTheme", "FlatSolarizedLightIJTheme", "FlatSpacegrayIJTheme",
            "FlatVuesionIJTheme", "FlatXcodeDarkIJTheme", "FlatAtomOneDarkIJTheme",
            "FlatAtomOneLightIJTheme", "FlatGitHubIJTheme", "FlatGitHubDarkIJTheme",
            "FlatLightOwlIJTheme", "FlatMaterialDarkerIJTheme", "FlatMaterialDeepOceanIJTheme",
            "FlatMaterialLighterIJTheme", "FlatMaterialOceanicIJTheme", "FlatMaterialPalenightIJTheme",
            "FlatMoonlightIJTheme", "FlatNightOwlIJTheme"
    };

    /*
    https://www.formdev.com/flatlaf/themes/
    https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#themes
    https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#how-to-use

     */

    public static void applyTheme(String theme, JFrame frame) {

        Dimension oldSize = frame.getSize();


        try {
            switch (theme) {
                case "Swing (Metal)":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
                case "FlatArcIJTheme":
                    UIManager.setLookAndFeel(new FlatArcIJTheme());
                    break;
                case "FlatArcOrangeIJTheme":
                    UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
                    break;
                case "FlatArcDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatArcDarkIJTheme());
                    break;
                case "FlatArcDarkOrangeIJTheme":
                    UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());
                    break;
                case "FlatCarbonIJTheme":
                    UIManager.setLookAndFeel(new FlatCarbonIJTheme());
                    break;
                case "FlatCobalt2IJTheme":
                    UIManager.setLookAndFeel(new FlatCobalt2IJTheme());
                    break;
                case "FlatCyanLightIJTheme":
                    UIManager.setLookAndFeel(new FlatCyanLightIJTheme());
                    break;
                case "FlatDarkFlatIJTheme":
                    UIManager.setLookAndFeel(new FlatDarkFlatIJTheme());
                    break;
                case "FlatDarkPurpleIJTheme":
                    UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
                    break;
                case "FlatDraculaIJTheme":
                    UIManager.setLookAndFeel(new FlatDraculaIJTheme());
                    break;
                case "FlatGradiantoDarkFuchsiaIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoDarkFuchsiaIJTheme());
                    break;
                case "FlatGradiantoDeepOceanIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoDeepOceanIJTheme());
                    break;
                case "FlatGradiantoMidnightBlueIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoMidnightBlueIJTheme());
                    break;
                case "FlatGradiantoNatureGreenIJTheme":
                    UIManager.setLookAndFeel(new FlatGradiantoNatureGreenIJTheme());
                    break;
                case "FlatGrayIJTheme":
                    UIManager.setLookAndFeel(new FlatGrayIJTheme());
                    break;
                case "FlatGruvboxDarkHardIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkHardIJTheme());
                    break;
                case "FlatGruvboxDarkMediumIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkMediumIJTheme());
                    break;
                case "FlatGruvboxDarkSoftIJTheme":
                    UIManager.setLookAndFeel(new FlatGruvboxDarkSoftIJTheme());
                    break;
                case "FlatHiberbeeDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatHiberbeeDarkIJTheme());
                    break;
                case "FlatHighContrastIJTheme":
                    UIManager.setLookAndFeel(new FlatHighContrastIJTheme());
                    break;
                case "FlatLightFlatIJTheme":
                    UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
                    break;
                case "FlatMaterialDesignDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDesignDarkIJTheme());
                    break;
                case "FlatMonocaiIJTheme":
                    UIManager.setLookAndFeel(new FlatMonocaiIJTheme());
                    break;
                case "FlatMonokaiProIJTheme":
                    UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());
                    break;
                case "FlatNordIJTheme":
                    UIManager.setLookAndFeel(new FlatNordIJTheme());
                    break;
                case "FlatOneDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
                    break;
                case "FlatSolarizedDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatSolarizedDarkIJTheme());
                    break;
                case "FlatSolarizedLightIJTheme":
                    UIManager.setLookAndFeel(new FlatSolarizedLightIJTheme());
                    break;
                case "FlatSpacegrayIJTheme":
                    UIManager.setLookAndFeel(new FlatSpacegrayIJTheme());
                    break;
                case "FlatVuesionIJTheme":
                    UIManager.setLookAndFeel(new FlatVuesionIJTheme());
                    break;
                case "FlatXcodeDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
                    break;
                case "FlatAtomOneDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatAtomOneDarkIJTheme());
                    break;
                case "FlatAtomOneLightIJTheme":
                    UIManager.setLookAndFeel(new FlatAtomOneLightIJTheme());
                    break;
                case "FlatGitHubIJTheme":
                    UIManager.setLookAndFeel(new FlatGitHubIJTheme());
                    break;
                case "FlatGitHubDarkIJTheme":
                    UIManager.setLookAndFeel(new FlatGitHubDarkIJTheme());
                    break;
                case "FlatLightOwlIJTheme":
                    UIManager.setLookAndFeel(new FlatLightOwlIJTheme());
                    break;
                case "FlatMaterialDarkerIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDarkerIJTheme());
                    break;
                case "FlatMaterialDeepOceanIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialDeepOceanIJTheme());
                    break;
                case "FlatMaterialLighterIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialLighterIJTheme());
                    break;
                case "FlatMaterialOceanicIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialOceanicIJTheme());
                    break;
                case "FlatMaterialPalenightIJTheme":
                    UIManager.setLookAndFeel(new FlatMaterialPalenightIJTheme());
                    break;
                case "FlatMonokaiProIJTheme2":
                    UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());
                    break;
                case "FlatMoonlightIJTheme":
                    UIManager.setLookAndFeel(new FlatMoonlightIJTheme());
                    break;
                case "FlatNightOwlIJTheme":
                    UIManager.setLookAndFeel(new FlatNightOwlIJTheme());
                    break;
                case "FlatSolarizedDarkIJTheme2":
                    UIManager.setLookAndFeel(new FlatSolarizedDarkIJTheme());
                    break;
                case "FlatSolarizedLightIJTheme2":
                    UIManager.setLookAndFeel(new FlatSolarizedLightIJTheme());
                    break;
                case "FlatLightLaf":
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                case "FlatDarkLaf":
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
                case "FlatIntelliJLaf":
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
                case "FlatDarculaLaf":
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    break;

                default:
                    return;
            }



                    // Arayüzü güncelle
            SwingUtilities.updateComponentTreeUI(frame);

            // Pencere boyutunu eski haline getir
            frame.setSize(oldSize);
            frame.setPreferredSize(oldSize);
            frame.pack(); // UI elemanlarını günceller

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
