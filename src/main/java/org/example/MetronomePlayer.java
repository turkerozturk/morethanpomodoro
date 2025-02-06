package org.example;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MetronomePlayer {

    private final int tickInterval;       // Kaç tick'te bir ses çalınacak
    private int counter = 0;
    private final String soundType;       // "MIDI" veya "WAV"
    private final String soundFile;       // "beep.wav" vb.

    private boolean isMuted = false;
    private boolean isRandomEnabled = false;  // Rastgele çalma özelliği //TODO

    private int remainingTicksToPlay = 0;
    private int remainingTicksToMute = 0;
    private final Random random = new Random();

    int midiInstrument;// = 9; // percussion, drums, bells etc.
    int midiNote;// = 60;
    int midiVolume;// = 30;
    private int wavSoundVolume;



    public MetronomePlayer(int tickInterval, String soundType, String soundFile, boolean isRandomEnabled) {
        this.tickInterval = tickInterval;
        this.soundType = soundType != null ? soundType : "WAV";
        this.soundFile = soundFile != null ? soundFile : "beep.wav";
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
        if ("MIDI".equalsIgnoreCase(soundType)) {
            playMidiNote();
        } else if ("WAV".equalsIgnoreCase(soundType)) {
            playWavFile();
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

    public int getWavSoundVolume() {
        return wavSoundVolume;
    }

    public void setWavSoundVolume(int wavSoundVolume) {
        this.wavSoundVolume = wavSoundVolume;
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
