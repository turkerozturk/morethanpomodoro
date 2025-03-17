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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AudioPropertiesDialog extends JDialog {

    public AudioPropertiesDialog(Frame parent, Map<Object, Object> audioPropertiesMap) {
        super(parent, "Audio Properties", true);

        // Tablo modeli oluştur (2 sütun: Key - Value)
        String[] columnNames = {"Property", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Map içeriğini tabloya ekle
        for (Map.Entry<Object, Object> entry : audioPropertiesMap.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey().toString(), entry.getValue().toString()});
            //System.out.println(entry.getKey().toString() + ": " + entry.getValue().toString());
        }


        // JTable oluştur
        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true); // Sıralama desteği ekle
        table.setShowGrid(true); // Hücre çizgilerini göster
        table.setGridColor(Color.LIGHT_GRAY); // Çizgi rengini belirle
        table.setRowHeight(25); // Satır yüksekliği

        // Sütun genişliklerini ayarla (ilk sütun dar, ikinci sütun geniş)
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Sütun genişliği kullanıcı tarafından değiştirilebilir
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Scroll desteği ekle
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        // Diyalog ayarları
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
    }

    public static void showAudioPropertiesDialog(Frame parent, Map<Object, Object> audioPropertiesMap) {
        AudioPropertiesDialog dialog = new AudioPropertiesDialog(parent, audioPropertiesMap);
        dialog.setVisible(true);
    }
}
