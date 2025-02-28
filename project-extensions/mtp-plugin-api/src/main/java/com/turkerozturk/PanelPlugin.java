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

/* bilgi: her plugin projesinde src/main/resources/META-INF/services/ klasoru olusturacaksin.
ve klasorun icin bu interface dosyasinin buradaki tam yoluile ayni adda
 yani com.turkerozturk.plugin.PanelPlugin adinda bir dosya olusturacaksin. Yani asagidaki gibi:
src/main/resources/META-INF/services/com.turkerozturk.plugin.PanelPlugin
Ve olusturdugun dosyanin icine o plugin projesinde bu interface i kullanan sinif veya siniflarin
isimlerini ekleyeceksin, mesela: com.turkerozturk.CountdownTimerPanel yazarsan artik ana uygulama tarafindan
plugin uygulmasindaki CountdownTimerPanel sinifi kullanilabilir olur.
 */
public interface PanelPlugin {
    String getTabName();         // Sekme başlığını döndür
    JPanel getPanel();           // Eklenti panelini döndür
}
