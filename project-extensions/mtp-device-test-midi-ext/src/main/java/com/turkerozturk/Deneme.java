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
package com.turkerozturk;

import javax.sound.midi.MidiUnavailableException;
import java.util.List;

public class Deneme {

    public static void main(String[] args) {
        MidiService midiService = new MidiService();

        try {
            // 1) List devices
            List<MidiDeviceInfoDTO> devices = midiService.listMidiDevices();
            // For demo, just pick the first device that is a synthesizer
            int chosenDeviceId = devices.get(0).getId();

            // 2) List instruments
            List<MidiInstrumentDTO> instruments = midiService.listInstruments(chosenDeviceId);
            // Again, just pick the first instrument
            int chosenInstrument = instruments.get(0).getId();

            // 3) Play a note
            midiService.playNote(chosenDeviceId, chosenInstrument, 60); // 60 = Middle C

        } catch (MidiUnavailableException mue) {
            System.err.println("MIDI error: " + mue.getMessage());
        }
    }

}
