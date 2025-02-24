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
package org.example;


import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiService {





    // We'll keep references to each device in a map so we can retrieve it later by ID.
    private final Map<Integer, MidiDevice.Info> deviceMap = new HashMap<>();

    /**
     * Lists all MIDI devices with an incremental integer ID and name.
     *
     * @return List of MidiDeviceInfoDTO
     * @throws MidiUnavailableException if MIDI subsystem is unavailable
     */
    public List<MidiDeviceInfoDTO> listMidiDevices() throws MidiUnavailableException {
        deviceMap.clear();
        List<MidiDeviceInfoDTO> result = new ArrayList<>();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        if (infos.length == 0) {
            // No devices found
            throw new MidiUnavailableException("No MIDI devices found on this system.");
        }

        int idCounter = 1;
        for (MidiDevice.Info info : infos) {
            // Some devices may not open correctly or may not be real synthesizers;
            // but we list them anyway. The user can choose, and we handle logic later.
            deviceMap.put(idCounter, info);
            result.add(new MidiDeviceInfoDTO(idCounter, info.getName()));
            idCounter++;
        }
        return result;
    }

    /**
     * Lists the instruments for a given device ID.
     * Only returns instruments if the device is a Synthesizer.
     *
     * @param deviceId The ID from listMidiDevices()
     * @return a list of MidiInstrumentDTO
     * @throws MidiUnavailableException if device can't be opened
     * @throws IllegalArgumentException if device ID is invalid or device is not a Synthesizer
     */
    public List<MidiInstrumentDTO> listInstruments(int deviceId) throws MidiUnavailableException {
        MidiDevice.Info info = deviceMap.get(deviceId);
        if (info == null) {
            throw new IllegalArgumentException("Invalid device ID: " + deviceId);
        }

        // Try to get the device
        MidiDevice device = MidiSystem.getMidiDevice(info);
        if (!(device instanceof Synthesizer)) {
            throw new IllegalArgumentException("Selected device is not a Synthesizer (no instruments).");
        }

        Synthesizer synth = (Synthesizer) device;
        // We must open the synthesizer to get the instruments
        boolean alreadyOpen = synth.isOpen();
        if (!alreadyOpen) {
            synth.open();
        }

        Soundbank soundbank = synth.getDefaultSoundbank();
        if (soundbank == null) {
            // Some synthesizers might not have a default soundbank
            throw new MidiUnavailableException("No default Soundbank available on this synthesizer.");
        }

        Instrument[] instruments = soundbank.getInstruments();
        List<MidiInstrumentDTO> result = new ArrayList<>();

        for (Instrument inst : instruments) {
            // Some instruments might be placeholders.
            // We'll do a simplistic check—if instrument name is empty or suspicious, skip.
            String name = inst.getName();
            if (name != null && !name.trim().isEmpty()) {
                int program = inst.getPatch().getProgram();
                result.add(new MidiInstrumentDTO(program, name));
            }
        }

        // If we had to open the synth, close it after reading instruments
        // (optional: or keep it open if you prefer)
        if (!alreadyOpen) {
            synth.close();
        }

        return result;
    }

    /**
     * Plays a short note on a given device's instrument.
     * This is just a quick example that sets the instrument, starts a note,
     * waits briefly, and then stops the note.
     *
     * @param deviceId    ID from listMidiDevices() that is a Synthesizer
     * @param instrumentProgram The program (ID) from listInstruments()
     * @param note        The MIDI note number (60 = Middle C, etc.)
     */
// Asenkron olarak notayı çalmak için bir sarmalayıcı metod yazalım:
    public void playNoteAsync(int deviceId, int instrumentProgram, int note) {
        new Thread(() -> {
            playNote(deviceId, instrumentProgram, note);
        }).start();
    }

    // Mevcut playNote metodunu, notayı çaldıktan sonra Synthesizer vb. kaynakları
// kapatacak şekilde try-finally bloğuna alalım:
    public void playNote(int deviceId, int instrumentProgram, int note) {
        Synthesizer synth = null;
        try {
            MidiDevice.Info info = deviceMap.get(deviceId);
            if (info == null) {
                throw new IllegalArgumentException("Invalid device ID: " + deviceId);
            }

            MidiDevice device = MidiSystem.getMidiDevice(info);
            if (!(device instanceof Synthesizer)) {
                throw new IllegalArgumentException("Selected device is not a Synthesizer.");
            }

            // Synthesizer'ı açıyoruz
            synth = (Synthesizer) device;
            if (!synth.isOpen()) {
                synth.open();
            }

            // Enstrümanı yüklüyoruz
            Soundbank sb = synth.getDefaultSoundbank();
            if (sb == null) {
                throw new IllegalArgumentException("No default soundbank found.");
            }
            Instrument[] instruments = sb.getInstruments();
            for (Instrument inst : instruments) {
                if (inst.getPatch().getProgram() == instrumentProgram) {
                    synth.loadInstrument(inst);
                    break;
                }
            }

            // MidiChannel ayarları
            MidiChannel[] channels = synth.getChannels();
            if (channels.length == 0) {
                throw new MidiUnavailableException("No MIDI channels available on this Synthesizer.");
            }
            MidiChannel channel = channels[0];
            channel.programChange(instrumentProgram);

            // Notayı çal
            channel.noteOn(note, 80);   // note numarası + velocity
            Thread.sleep(1000);         // 500ms bekleyelim
            channel.noteOff(note);     // Notayı sustur

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Metottan çıkmadan önce Synthesizer açıksa kapatalım
            if (synth != null && synth.isOpen()) {
                synth.close();
            }
        }
    }

}
