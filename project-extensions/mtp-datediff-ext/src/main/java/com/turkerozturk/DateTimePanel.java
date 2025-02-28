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
package com.turkerozturk;

import com.turkerozturk.initial.ExtensionCategory;
import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class DateTimePanel extends JPanel implements PanelPlugin{

    private JDatePickerImpl startDatePicker, endDatePicker;
    private JLabel resultLabel, totalDaysLabel;

    public DateTimePanel() {
        setLayout(new GridLayout(3, 2, 10, 10));

        // Tarih seçicileri oluştur
        startDatePicker = createDatePicker();
        endDatePicker = createDatePicker();

        // Etiketler
        JLabel startLabel = new JLabel("Start Date:");
        JLabel endLabel = new JLabel("End Date:");
        resultLabel = new JLabel("Difference: ");
        totalDaysLabel = new JLabel("Total Days: ");

        // Dinleyicileri ekle (Tarih seçildiğinde farkı hesapla)
        startDatePicker.addActionListener(e -> calculateDifference());
        endDatePicker.addActionListener(e -> calculateDifference());

        // Panele ekleme
        add(startLabel);
        add(startDatePicker);
        add(endLabel);
        add(endDatePicker);
        add(resultLabel);
        add(totalDaysLabel);
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void calculateDifference() {
        LocalDate startDate = getSelectedDate(startDatePicker);
        LocalDate endDate = getSelectedDate(endDatePicker);

        if (startDate != null && endDate != null) {
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long years = ChronoUnit.YEARS.between(startDate, endDate);
            long months = ChronoUnit.MONTHS.between(startDate.plusYears(years), endDate);
            long days = ChronoUnit.DAYS.between(startDate.plusYears(years).plusMonths(months), endDate);

            resultLabel.setText(String.format("Difference: %d years, %d months, %d days", years, months, days));
            totalDaysLabel.setText("Total Days: " + totalDays);
        }
    }

    private LocalDate getSelectedDate(JDatePickerImpl datePicker) {
        if (datePicker.getModel().getValue() != null) {
            return ((java.util.Date) datePicker.getModel().getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DateTime Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new DateTimePanel());
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.date.diff.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.OTHER;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
