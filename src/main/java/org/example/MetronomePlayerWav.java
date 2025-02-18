package org.example;

import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;



import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.awt.Toolkit;
import java.io.*;
import java.net.URL;
import java.util.Random;

public class MetronomePlayerWav {

    private final LanguageManager bundle = LanguageManager.getInstance();
    private static final ConfigManager props = ConfigManager.getInstance();

    private static Clip clip;

    static {
        try {
            String fileName = props.getProperty("pomodoro.tick.sound.wav.file");

            AudioInputStream audioStream = null;

            // 1. Çalışma dizininde dosya var mı kontrol et
            File externalFile = new File(fileName);
            //System.out.println(fileName);
            if (externalFile.exists()) {
                audioStream = AudioSystem.getAudioInputStream(externalFile);
            } else {
                // 2. Resources içindeki dosyayı kontrol et
                URL resource = MetronomePlayerWav.class.getClassLoader().getResource(fileName);
                if (resource != null) {
                    audioStream = AudioSystem.getAudioInputStream(resource);
                }
            }

            // 3. Hiçbir dosya bulunamazsa, bellekte beep ses oluştur
            if (audioStream == null) {
                audioStream = createBeepAudioStream();
            }

            // Clip oluştur ve ses akışını yükle
            clip = AudioSystem.getClip();
            clip.open(audioStream);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bellek üzerinde bir beep sesi üreten metot.
     * 500 ms süresinde, 1000 Hz frekansında 16-bit PCM mono ses üretilir.
     */
    private static AudioInputStream createBeepAudioStream() {
        int sampleRate = 44100;
        int durationMs = 500;
        int numSamples = durationMs * sampleRate / 1000;
        byte[] data = new byte[numSamples * 2]; // 16-bit PCM, her örnek 2 byte

        double frequency = 1000; // 1000 Hz beep tonu
        for (int i = 0; i < numSamples; i++) {
            double t = i / (double) sampleRate;
            // Sinüs dalgası üretimi
            short sample = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * frequency * t));
            data[2 * i] = (byte) (sample & 0xff);
            data[2 * i + 1] = (byte) ((sample >> 8) & 0xff);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        return new AudioInputStream(bais, format, numSamples);
    }


    private int tickSoundVolume;

    public void playWavSoundFromMemory() {
        if (clip != null) {
            clip.setFramePosition(0); // Sesi başa sar

            // Ses seviyesi ayarı
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Desibel ölçeğine dönüştürme (örnek -80 dB ile 0 dB arasında)
            float min = volumeControl.getMinimum(); // Genellikle -80 dB
            float max = 0.0f; // Maksimum ses seviyesi genellikle 0 dB

            // tickSoundVolume'u 0-100 arasında dB aralığına doğrusal ölçekle
            // float gain = min + ((tickSoundVolume / 100.0f) * (max - min));

            // Logaritmik ses seviyesini hesapla
            float gain = (float) (min + (max - min) * Math.log10(1 + (tickSoundVolume * 9.0) / 100));


            volumeControl.setValue(gain); // bilgi: this 1 time loop trick is worked to hear continuous ticks.
            clip.loop(1);
            clip.start(); // Çalmaya başla
        }
    }













    private final int tickInterval;       // Kaç tick'te bir ses çalınacak
    private int counter = 0;
    private final String soundType;       // "MIDI" veya "WAV"
    private final String soundFile;       // "beep.wav" vb.

    private boolean isMuted = false;
    private boolean isRandomEnabled;

    private int remainingTicksToPlay = 0;
    private int remainingTicksToMute = 0;
    private final Random random = new Random();

    int midiInstrument;// = 9; // percussion, drums, bells etc.
    int midiNote;// = 60;
    int midiVolume;// = 30;
    private int wavSoundVolume;



    public MetronomePlayerWav(int tickInterval, String soundType, String soundFile, boolean isRandomEnabled) {
        this.tickInterval = tickInterval;
        this.soundType = soundType != null ? soundType : "WAV";
        this.soundFile = soundFile != null ? soundFile : "tick.wav";
        this.isRandomEnabled = isRandomEnabled;
        resetRandomTicks();
    }

    public int getMidiVolume() {
        return midiVolume;
    }

    public void setMidiVolume(int midiVolume) {
        this.midiVolume = midiVolume;
    }

    public void setMidiInstrumentAndNote(int midiInstrument, int midiNote) {
        this.midiInstrument = midiInstrument;
        this.midiNote = midiNote;
    }

    public void tick() {
        counter++;
        if (counter >= tickInterval) {
            if (!isMuted) {
                if (isRandomEnabled) {
                    if (remainingTicksToMute > 0) {
                        remainingTicksToMute--;
                    } else {
                        playSound();
                        remainingTicksToPlay--;

                        if (remainingTicksToPlay <= 0) {
                            resetRandomTicks();
                        }
                    }
                } else {
                    playSound();
                }
            }
            counter = 0;
        }
    }

    private void resetRandomTicks() {
        remainingTicksToPlay = random.nextInt(16) + 1; // 1-10 arasında rastgele çalma süresi
        remainingTicksToMute = (counter == 0) ? 0 : random.nextInt(4) + 1;  // İlk başta 0, sonrasında rastgele
    }

    private void playSound() {
        //System.out.println(soundType);
        if ("MIDI".equalsIgnoreCase(soundType)) {
            playMidiNote();
            //playTickJFugue();
            //playWavFile();
        } else if ("WAV".equalsIgnoreCase(soundType)) {
            playWavSoundFromMemory();

           // playWavFile();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void playMidiNote() {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            Sequence sequence = new Sequence(Sequence.PPQ, 4);
            Track track = sequence.createTrack();


            //track.add(createMidiEvent(ShortMessage.NOTE_OFF, 9, 49, 0, 1));
            int tickDuration = 4; // Notanın çalınacağı süre (ölçü birimi: tick)

            // NOTA BAŞLAT (NOTE_ON)
            track.add(createMidiEvent(ShortMessage.NOTE_ON, midiInstrument, midiNote, midiVolume, 0));

            // NOTA DURDUR (NOTE_OFF), belirtilen tick süresi sonra
            track.add(createMidiEvent(ShortMessage.NOTE_OFF, midiInstrument, midiNote, 0, tickDuration));


            sequencer.setSequence(sequence);
            sequencer.start();

            new Thread(() -> {
                try { Thread.sleep(999); } catch (InterruptedException e) { e.printStackTrace(); }
                sequencer.stop();
                sequencer.close();
            }).start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private MidiEvent createMidiEvent(int command, int channel, int note, int velocity, long tick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(command, channel, note, velocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return new MidiEvent(message, tick);
    }

    private void playWavFile() {
        try (InputStream audioSrc = getClass().getResourceAsStream("/" + soundFile)) {
            if (audioSrc == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc))) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);

                // Ses seviyesi ayarı
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = min + (wavSoundVolume * (max - min)); // 0.0 - 1.0 arasında normalleştirme
                volumeControl.setValue(gain);

                clip.start();
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void setMuted(boolean mute) {
        this.isMuted = mute;
    }

    public void setRandomEnabled(boolean randomEnabled) {
        this.isRandomEnabled = randomEnabled;
        resetRandomTicks();
    }

    /*
    public int getWavSoundVolume() {
        return wavSoundVolume;
    }

    public void setWavSoundVolume(int wavSoundVolume) {
        this.wavSoundVolume = wavSoundVolume;
    }
    */

    public void setVolume(int tickSoundVolume) {
        this.tickSoundVolume = tickSoundVolume;
    }

    /*
    public static void main(String[] args) throws InterruptedException {
        MetronomePlayer metronome = new MetronomePlayer(5, "WAV", "beep.wav", true);

        // 50 tick boyunca çalıştır
        for (int i = 0; i < 50; i++) {
            metronome.tick();
            Thread.sleep(200);  // Tick hızını simüle etmek için 200ms bekle
        }
    }
*/



}
