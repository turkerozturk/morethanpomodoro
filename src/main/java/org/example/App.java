package org.example;


import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel( new FlatSolarizedLightIJTheme() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        SwingUtilities.invokeLater(() -> {
            new ApplicationFrame().setVisible(true);
        });
    }}
