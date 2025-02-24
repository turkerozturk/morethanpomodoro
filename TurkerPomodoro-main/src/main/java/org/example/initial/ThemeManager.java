package org.example.initial;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;
import org.example.ApplicationFrame;

import javax.swing.*;

public class ThemeManager {


    private static final ConfigManager props = ConfigManager.getInstance();


    public static void loadTheme() {
        try {


            // Konfigürasyondan tema ismini al
            String themeName = props.getProperty("gui.theme.template.name");

            // FlatLaf ile uygun temayı belirle
            UIManager.LookAndFeelInfo[] installedLafs = UIManager.getInstalledLookAndFeels();
            boolean themeSet = false;

            for (UIManager.LookAndFeelInfo laf : installedLafs) {
                if (laf.getClassName().contains(themeName)) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    themeSet = true;
                    break;
                }
            }

            // Eğer varsayılan LookAndFeel ile eşleşmezse, FlatLaf temalarını manuel yükleyelim
            if (!themeSet) {
                switch (themeName) {
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
                    case "FlatDarkLaf":
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                        break;
                    default:
                        UIManager.setLookAndFeel(new FlatLightLaf());
                }
            }

            // koseleri yuvarlatmak icin:
            UIManager.put( "Button.arc", 15 );
            UIManager.put( "Component.arc", 15 );
            UIManager.put( "ProgressBar.arc", 15 );
            UIManager.put( "TextComponent.arc", 15 );
            UIManager.put( "ScrollBar.showButtons", true );
            UIManager.put( "ScrollBar.width", 10 );
            UIManager.put( "TabbedPane.tabSeparatorsFullHeight", true );

            // FlatLaf'ı güncelle (UI'yi yeniden boyamak için)
            FlatLaf.updateUI();
           // System.out.println("Theme applied: " + themeName);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load theme, using default.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            loadTheme();
            JFrame frame = new ApplicationFrame(); // Ana pencereyi başlat
            frame.setVisible(true);
        });
    }
}
