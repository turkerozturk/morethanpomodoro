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
// Özel paste devre dışı bırakılan metin alanı
package org.example.jpanels.configuration;

import javax.swing.*;
import java.awt.*;

public class NoPasteTextField extends JTextField {
    public NoPasteTextField(int columns) {
        super(columns);
    }

    @Override
    public void paste() {
        // Paste işlemini devre dışı bırakıyoruz
        Toolkit.getDefaultToolkit().beep(); // Kullanıcıya uyarı sesi verir.
        System.out.println("Paste işlemi devre dışı bırakıldı.");
    }
}
