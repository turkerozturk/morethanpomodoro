package com.turkerozturk.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class AlwaysOnTopButton extends JToggleButton {

    boolean isAlwaysOnTop;

    int iconWidth, iconHeight;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();
    public AlwaysOnTopButton() {
        super("");
        setFocusable(false);

        isAlwaysOnTop = Integer.parseInt(props.getProperty("gui.is.always.on.top")) == 1;
        iconWidth = Integer.parseInt(props.getProperty("gui.icon.width"));
        iconHeight = Integer.parseInt(props.getProperty("gui.icon.height"));

        this.setToolTipText(bundle.getString("frame.always.on.top"));
        FlatSVGIcon icon = new FlatSVGIcon("svg/always-on-top__iconduck__top-with-upwards-arrow-above.svg", iconWidth, iconHeight);
        this.setIcon(icon);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = getParentWindow();
                if (window != null) {
                    window.setAlwaysOnTop(isSelected());
                }
            }
        });
    }

    private Window getParentWindow() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof Window) {
                return (Window) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
}
