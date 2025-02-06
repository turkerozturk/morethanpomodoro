package org.example;

import javax.sound.midi.*;

public class Metronome {
    private Sequencer sequencer;
    private int bpm = 120;
    private float volume = 0.5f;
    private boolean playing = false;

    public Metronome() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void start(int bpm) {
        if (playing) return;
        this.bpm = bpm;
        playing = true;

        new Thread(() -> {
            try {
                Sequence sequence = new Sequence(Sequence.PPQ, 24);
                Track track = sequence.createTrack();

                ShortMessage message = new ShortMessage();
                message.setMessage(ShortMessage.NOTE_ON, 9, 59, (int) (volume * 127));
                MidiEvent event = new MidiEvent(message, 1);
                track.add(event);

                message = new ShortMessage();
                message.setMessage(ShortMessage.NOTE_OFF, 9, 59, 0);
                event = new MidiEvent(message, 24);
                track.add(event);

                sequencer.setSequence(sequence);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.setTempoInBPM(bpm);
                sequencer.start();

                while (playing) {
                    Thread.sleep(100);
                }

                sequencer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        playing = false;
    }

    public void setBPM(int bpm) {
        this.bpm = bpm;
        if (playing) {
            sequencer.setTempoInBPM(bpm);
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
