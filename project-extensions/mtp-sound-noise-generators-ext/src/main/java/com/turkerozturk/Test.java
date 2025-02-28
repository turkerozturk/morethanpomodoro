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

public class Test {
    public static void main(String[] args) {
        // BrownNoise main() metodunu normalde çalıştırdığınız gibi çalıştırın
        // Örn: Tüm kod yukarıdaki BrownNoise sınıfında.

        // Volume'u %0 ila %100 aralığında ayarlama
        BrownNoise.setVolumePercent(50);  // Yüzde 50

        // Veya doğrudan dB olarak ayarlama
        BrownNoise.setVolumeDb(-30);      // -30 dB

        // BrownNoise main() ...
        // (komut satırında veya IDE'de bu sınıfı doğrudan çalıştırabilirsiniz)
    }
}
