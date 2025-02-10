package org.example.jpanels.datetime;

import org.jdatepicker.impl.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class DateTimePanel extends JPanel {

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
}
