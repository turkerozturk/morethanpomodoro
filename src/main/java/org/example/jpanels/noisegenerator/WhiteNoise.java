package org.example.jpanels.noisegenerator;

import java.nio.ByteBuffer;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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