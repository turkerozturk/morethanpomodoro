package org.example.jpanels.mididevice;

/**
 * Simple DTO to hold MIDI instrument info.
 */
public class MidiInstrumentDTO {
    private final int id;       // Program number
    private final String name;

    public MidiInstrumentDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}