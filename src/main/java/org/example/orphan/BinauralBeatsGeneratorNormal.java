package org.example.orphan;

import javax.sound.sampled.*;

public class BinauralBeatsGeneratorNormal {
    private static final int SAMPLE_RATE = 44100; // CD kalitesi

    public static void generateBinauralBeats(double baseFrequency, double beatFrequency, int durationSeconds) {
        int numSamples = SAMPLE_RATE * durationSeconds;
        byte[] audioBuffer = new byte[4 * numSamples]; // Düzeltildi: 2 kanal x 2 byte (16-bit PCM)

        for (int i = 0; i < numSamples; i++) {
            double time = i / (double) SAMPLE_RATE;

            // Sol kanal (baseFrequency)
            double leftSample = Math.sin(2 * Math.PI * baseFrequency * time);

            // Sağ kanal (baseFrequency + beatFrequency)
            double rightSample = Math.sin(2 * Math.PI * (baseFrequency + beatFrequency) * time);

            // 16-bit PCM dönüşümü
            short leftValue = (short) (leftSample * Short.MAX_VALUE);
            short rightValue = (short) (rightSample * Short.MAX_VALUE);

            // Stereo için her kanal 2 byte olarak diziye yazılıyor
            int index = i * 4;
            audioBuffer[index] = (byte) (leftValue & 0xff);
            audioBuffer[index + 1] = (byte) ((leftValue >> 8) & 0xff);
            audioBuffer[index + 2] = (byte) (rightValue & 0xff);
            audioBuffer[index + 3] = (byte) ((rightValue >> 8) & 0xff);
        }

        playSound(audioBuffer);
    }

    private static void playSound(byte[] audioBuffer) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            line.write(audioBuffer, 0, audioBuffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 440 Hz sol kanal, 445 Hz sağ kanal (5 Hz binaural beat)
       // generateBinauralBeats(440, 5, 10);

        generateBinauralBeats(528, 5, 10);


    }
}
