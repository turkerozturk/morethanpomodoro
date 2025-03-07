package com.turkerozturk;

import com.turkerozturk.initial.ExtensionCategory;

import javax.swing.*;
import java.awt.*;

public class AnalogClockPanel extends JPanel implements PanelPlugin {

    public AnalogClockPanel() {

        // Varsayılan olarak JPanel, FlowLayout kullanır.
        // FlowLayout bileşenleri içerik kadar genişletir, sekmeyi tamamen doldurmaz.
        // Bunun yerine BorderLayout kullanalım.
        setLayout(new BorderLayout());

        AnalogClock analogClock = new AnalogClock();
        add(analogClock);

    }

    @Override
    public String getTabName() {
        return "plugin.clock.analog.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.INFO;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }

}

