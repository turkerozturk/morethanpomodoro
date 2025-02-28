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

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class WhiteNoise {
    public static void main(String[] args) {
        final int SAMPLE_RATE = 44100;
        final int BITS = 16;
        final int CHANNELS = 1;
        final int SAMPLE_SIZE = 2;
        final int PACKET_SIZE = 5000;
        AudioFormat format = new AudioFormat(
                SAMPLE_RATE,
                BITS,
                CHANNELS,
                true, // signed
                true  // big endian
        );
        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                format,
                PACKET_SIZE * 2
        );
        SourceDataLine line;

        try {
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format);
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }

        line.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //line.drain(); // seems to hang my Windows machine
            line.close();
        }));
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        Random random = new Random();

        for (;;) {
            buffer.clear();

            for (int i = 0; i < PACKET_SIZE / SAMPLE_SIZE; i++) {
                buffer.putShort((short)(random.nextGaussian() * Short.MAX_VALUE));
            }

            line.write(buffer.array(), 0, buffer.position());
        }
    }
}
