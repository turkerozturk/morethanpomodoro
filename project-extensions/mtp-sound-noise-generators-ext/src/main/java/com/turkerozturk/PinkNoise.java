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

public class PinkNoise {
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


        double b0 = 0.0;
        double b1 = 0.0;
        double b2 = 0.0;
        double b3 = 0.0;
        double b4 = 0.0;
        double b5 = 0.0;
        double b6 = 0.0;

        for (;;) {
            buffer.clear();

            for (int i = 0; i < PACKET_SIZE / SAMPLE_SIZE; i++) {
                double white = random.nextGaussian();
                b0 = 0.99886 * b0 + white * 0.0555179;
                b1 = 0.99332 * b1 + white * 0.0750759;
                b2 = 0.96900 * b2 + white * 0.1538520;
                b3 = 0.86650 * b3 + white * 0.3104856;
                b4 = 0.55000 * b4 + white * 0.5329522;
                b5 = -0.7616 * b5 - white * 0.0168980;
                double output = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362;
                output *= 0.05; // (roughly) compensate for gain
                b6 = white * 0.115926;
                buffer.putShort((short)(output * Short.MAX_VALUE));
            }

            line.write(buffer.array(), 0, buffer.position());
        }
    }
}
