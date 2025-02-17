package org.example.jpanels.mididevice;

/**
 * Simple DTO (Data Transfer Object) to hold MIDI device info.
 */
public class MidiDeviceInfoDTO {
    private final int id;
    private final String name;

    public MidiDeviceInfoDTO(int id, String name) {
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
