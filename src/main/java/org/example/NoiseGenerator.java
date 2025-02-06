package org.example;

import javax.sound.sampled.*;
import java.util.Random;

public class NoiseGenerator {
    private SourceDataLine line;
    private Thread playThread;
    private boolean playing = false;
    private float volume = 0.5f;

    public void start(String noiseType) {
        if (playing) return;
        playing = true;

        playThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[1024];
                Random random = new Random();

                while (playing) {
                    generateNoise(buffer, noiseType, random);
                    adjustVolume(buffer);
                    line.write(buffer, 0, buffer.length);
                }

                line.drain();
                line.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    public void stop() {
        playing = false;
        if (playThread != null) {
            try {
                playThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    private void generateNoise(byte[] buffer, String noiseType, Random random) {
        switch (noiseType) {
            case "White Noise":
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = (byte) (random.nextInt(256) - 128);
                }
                break;
            case "Brown Noise":
                double brown = 0;
                for (int i = 0; i < buffer.length; i++) {
                    brown += (random.nextDouble() * 2 - 1) * 5;
                    brown = Math.max(-128, Math.min(127, brown));
                    buffer[i] = (byte) brown;
                }
                break;
            case "Pink Noise":
                double[] pink = new double[7];
                for (int i = 0; i < buffer.length; i++) {
                    pink[0] = 0.99886 * pink[0] + random.nextDouble() * 0.0555179;
                    pink[1] = 0.99332 * pink[1] + random.nextDouble() * 0.0750759;
                    pink[2] = 0.96900 * pink[2] + random.nextDouble() * 0.1538520;
                    pink[3] = 0.86650 * pink[3] + random.nextDouble() * 0.3104856;
                    pink[4] = 0.55000 * pink[4] + random.nextDouble() * 0.5329522;
                    pink[5] = -0.7616 * pink[5] - random.nextDouble() * 0.0168980;
                    double white = random.nextDouble() * 0.5362;
                    buffer[i] = (byte) ((pink[0] + pink[1] + pink[2] + pink[3] + pink[4] + pink[5] + white) * 64);
                }
                break;
        }
    }

    private void adjustVolume(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] *= volume;
        }
    }
}
