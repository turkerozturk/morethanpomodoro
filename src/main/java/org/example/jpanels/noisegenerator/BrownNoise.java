package org.example.jpanels.noisegenerator;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class BrownNoise {
    // Minimum ve maksimum desibel değerleri
    private static float minDb = -80.0f;
    private static float maxDb = 0.0f;

    // Desibel'den dönüştürülmüş (0.0 - 1.0 civarı) lineer volume değeri
    private static float linearVolume = 1.0f; // Başlangıçta 0 dB (1.0)

    public static void main(String[] args) {
        final int SAMPLE_RATE = 44100;
        final int BITS = 16;
        final int CHANNELS = 1;
        final int SAMPLE_SIZE = 2;    // 16 bit = 2 byte
        final int PACKET_SIZE = 5000;

        AudioFormat format = new AudioFormat(
                SAMPLE_RATE,
                BITS,
                CHANNELS,
                true,  // signed
                true   // big endian
        );
        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                format,
                PACKET_SIZE * 2
        );
        SourceDataLine line;

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }

        line.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            line.close();
        }));

        // Örnek kullanım: Ses seviyesini %25 yap
        // bu da setVolumeDb() metodunun içinden -80..0 dB aralığına map edilir.
        setVolumePercent(65);

        // veya doğrudan dB olarak da ayarlayabilirsiniz:
        // setVolumeDb(-40); // -40 dB

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        Random random = new Random();
        double lastOut = 0.0;

        // Sürekli olarak "Brown Noise" üreten döngü
        for (;;) {
            buffer.clear();

            for (int i = 0; i < PACKET_SIZE / SAMPLE_SIZE; i++) {
                // Brown Noise üretimi
                double white = random.nextGaussian();
                double output = (lastOut + (0.02 * white)) / 1.02;
                lastOut = output;
                output *= 1.5; // (roughly) compensate for gain

                // lineerVolume, dB'den hesaplanmıştır
                output *= linearVolume;

                // short aralığına (16-bit signed) dönüştür
                buffer.putShort((short)(output * Short.MAX_VALUE));
            }

            line.write(buffer.array(), 0, buffer.position());
        }
    }

    /**
     * 0-100 arasında bir değer aldığımızda, bunu minDb..maxDb aralığına
     * lineer olarak map ediyoruz. Ardından setVolumeDb()'yi çağırıyoruz.
     *
     * Örneğin: setVolumePercent(50) -> aradaki dB değerine dönüştürülür.
     */
    public static void setVolumePercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        // 0 -> minDb, 100 -> maxDb
        float dbValue = minDb + (maxDb - minDb) * (percent / 100.0f);
        setVolumeDb(dbValue);
    }

    /**
     * Ses seviyesini desibel (dB) cinsinden ayarlamak için.
     * minDb ve maxDb aralığında değerleri kısıtlar, sonra
     * 10^(dB/20) ile lineerVolume hesaplar.
     */
    public static void setVolumeDb(float db) {
        if (db < minDb) db = minDb;
        if (db > maxDb) db = maxDb;

        // dB -> lineer dönüşüm: volume = 10^(db/20)
        linearVolume = (float)Math.pow(10, db / 20.0);
    }
}
