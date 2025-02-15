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
            // https://www.formdev.com/flatlaf/customizing/
            UIManager.put( "Button.arc", 15 );
            UIManager.put( "Component.arc", 15 );
            UIManager.put( "ProgressBar.arc", 15 );
            UIManager.put( "TextComponent.arc", 15 );
            UIManager.put( "ScrollBar.showButtons", true );
            UIManager.put( "ScrollBar.width", 10 );
            UIManager.put( "TabbedPane.tabSeparatorsFullHeight", true );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        SwingUtilities.invokeLater(() -> {
            new ApplicationFrame().setVisible(true);
        });
    }}
