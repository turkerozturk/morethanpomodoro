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

import java.util.Map;

public class MP3DurationUtils {

    /**
     * MP3 süresini bulur (saniye cinsinden).
     *
     * @param audioProperties Map<Object, Object> türünde veriler
     * @return Saniye cinsinden süre. Değer bulunamazsa -1 döner.
     */
    public static double getDurationInSeconds(Map<Object, Object> audioProperties) {



/*
        // 2) "duration" -> mikrosaniyeden saniyeye
        if (audioProperties.containsKey("duration")) {
            try {
                long durationMicro = Long.parseLong(audioProperties.get("duration").toString());
                return durationMicro / 1_000_000.0;  // mikrosaniye -> saniye
            } catch (NumberFormatException e) {
                // parse hatası varsa diğer yöntemlere geç
            }
        }
*/
        /*
        // 3) Frame sayısı / Frame Rate yöntemi
        // mp3.length.frames veya audio.length.frames
        // mp3.framerate.fps veya audio.framerate.fps
        long frames = -1;
        double frameRate = -1;

        // Toplam frame sayısını bulalım
        if (audioProperties.containsKey("mp3.length.frames")) {
            try {
                frames = Long.parseLong(audioProperties.get("mp3.length.frames").toString());
            } catch (NumberFormatException ignored) {}
        } else if (audioProperties.containsKey("audio.length.frames")) {
            try {
                frames = Long.parseLong(audioProperties.get("audio.length.frames").toString());
            } catch (NumberFormatException ignored) {}
        }

        // Frame rate değerini bulalım
        if (audioProperties.containsKey("mp3.framerate.fps")) {
            try {
                frameRate = Double.parseDouble(audioProperties.get("mp3.framerate.fps").toString());
            } catch (NumberFormatException ignored) {}
        } else if (audioProperties.containsKey("audio.framerate.fps")) {
            try {
                frameRate = Double.parseDouble(audioProperties.get("audio.framerate.fps").toString());
            } catch (NumberFormatException ignored) {}
        }

        if (frames > 0 && frameRate > 0) {
            return frames / frameRate;
        }
*/
        // 4) Dosya boyutu / Bitrate yöntemi
        // (bitrate'i bit/saniye cinsinden bekliyoruz)
        long bytes = -1;
        long bitrate = -1;

        // Dosya büyüklüğü
        if (audioProperties.containsKey("mp3.length.bytes")) {
            try {
                bytes = Long.parseLong(audioProperties.get("mp3.length.bytes").toString());
            } catch (NumberFormatException ignored) {
            }
        } else if (audioProperties.containsKey("audio.length.bytes")) {
            try {
                bytes = Long.parseLong(audioProperties.get("audio.length.bytes").toString());
            } catch (NumberFormatException ignored) {
            }
        }

        // Bitrate
        if (audioProperties.containsKey("mp3.bitrate.nominal.bps")) {
            try {
                bitrate = Long.parseLong(audioProperties.get("mp3.bitrate.nominal.bps").toString());
            } catch (NumberFormatException ignored) {
            }
        } else if (audioProperties.containsKey("bitrate")) {
            try {
                bitrate = Long.parseLong(audioProperties.get("bitrate").toString());
            } catch (NumberFormatException ignored) {
            }
        }

        // Eğer bytes ve bitrate geçerliyse, süreyi hesapla
        if (bytes > 0 && bitrate > 0) {
            // Dosya boyutunu bit cinsine çevir, bitrate = bit/s
            return (bytes * 8.0) / bitrate;
        }

        // Hiçbir yöntemde başarı yoksa -1 döndür
        return -1;
    }


    /**
     * Saniye cinsinden süreyi HH:MM:SS formatına dönüştürür.
     * - Eğer saat yoksa sadece MM:SS olarak döner (opsiyonel tercih).
     */
    public static String formatDuration(double seconds) {
        if (seconds < 0) {
            return "Süre hesaplanamadı";
        }

        long totalSeconds = (long) seconds;  // Yakınsak, isterseniz yuvarlayabilirsiniz
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long secs = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
}
