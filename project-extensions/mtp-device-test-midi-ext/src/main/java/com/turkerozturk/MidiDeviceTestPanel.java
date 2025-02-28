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



import com.turkerozturk.initial.ExtensionCategory;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MidiDeviceTestPanel extends JPanel implements PanelPlugin{

    private final MidiService midiService;
    private JComboBox<MidiDeviceInfoDTO> deviceComboBox;
    private JComboBox<MidiInstrumentDTO> instrumentComboBox;
    private JSpinner noteSpinner;
    private JButton playButton;
    private JLabel messageLabel;

    public MidiDeviceTestPanel() {
        midiService = new MidiService();
        initComponents();
        loadDevices();
    }

    private void initComponents() {
        setLayout(new GridLayout(5, 2, 5, 5));

        // (1) Device ComboBox
        JLabel deviceLabel = new JLabel("MIDI Device:");
        deviceComboBox = new JComboBox<>();
        deviceComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Clear any previous error
                messageLabel.setText("");
                loadInstruments();
            }
        });

        // (2) Instrument ComboBox
        JLabel instrumentLabel = new JLabel("Instrument:");
        instrumentComboBox = new JComboBox<>();

        // (3) Note Spinner with min=0, max=127, step=1, default=60 (Middle C)
        JLabel noteLabel = new JLabel("MIDI Note:");
        noteSpinner = new JSpinner(new SpinnerNumberModel(60, 0, 127, 1));

        // (4) Play Button
        playButton = new JButton("Play Note");
        playButton.addActionListener(e -> onPlayNote());

        // (5) Error/Message Label
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED); // Display errors in red

        // Add components
        add(deviceLabel);
        add(deviceComboBox);

        add(instrumentLabel);
        add(instrumentComboBox);

        add(noteLabel);
        add(noteSpinner);

        // a placeholder label to align the button
        add(new JLabel(""));
        add(playButton);

        // final row for messages
        add(new JLabel("Message:"));
        add(messageLabel);
    }

    /**
     * Loads available MIDI devices into the device combo box
     * and clears the instrument list.
     */
    private void loadDevices() {
        try {
            List<MidiDeviceInfoDTO> devices = midiService.listMidiDevices();
            deviceComboBox.removeAllItems();
            for (MidiDeviceInfoDTO dto : devices) {
                deviceComboBox.addItem(dto);
            }

            // If devices exist, enable the device combo
            if (!devices.isEmpty()) {
                deviceComboBox.setEnabled(true);
            }
        } catch (MidiUnavailableException e) {
            messageLabel.setText("Error loading MIDI devices: " + e.getMessage());
            disableAllControls();
        }
    }

    /**
     * Loads instruments for the currently selected MIDI device.
     * Sort instruments ascending by program number, padded with zeros in the text.
     */
    private void loadInstruments() {
        instrumentComboBox.removeAllItems();
        MidiDeviceInfoDTO selectedDevice =
                (MidiDeviceInfoDTO) deviceComboBox.getSelectedItem();

        if (selectedDevice == null) {
            messageLabel.setText("No MIDI device selected.");
            disableAllControls();
            return;
        }

        try {
            List<MidiInstrumentDTO> instruments =
                    midiService.listInstruments(selectedDevice.getId());

            if (instruments.isEmpty()) {
                messageLabel.setText("No instruments found for this device.");
                disableAllControls();
                return;
            }

            // Sort instruments by their program ID ascending
            Collections.sort(instruments, Comparator.comparingInt(MidiInstrumentDTO::getId));

            // Add them, zero-padded in the display text
            for (MidiInstrumentDTO inst : instruments) {
                // E.g. "001: Piano" for program=1
                String paddedId = String.format("%03d", inst.getId());
                String displayText = paddedId + ": " + inst.getName();

                // We can store the actual object but show the custom text:
                instrumentComboBox.addItem(
                        new MidiInstrumentDTO(inst.getId(), displayText)
                );
            }

            enableAllControls();

        } catch (MidiUnavailableException | IllegalArgumentException ex) {
            messageLabel.setText("Error loading instruments: " + ex.getMessage());
            disableAllControls();
        }
    }

    /**
     * Triggered when user clicks on 'Play Note'.
     */
    private void onPlayNote() {
        messageLabel.setText("");

        MidiDeviceInfoDTO selectedDevice =
                (MidiDeviceInfoDTO) deviceComboBox.getSelectedItem();
        if (selectedDevice == null) {
            messageLabel.setText("No MIDI device selected.");
            disableAllControls();
            return;
        }

        MidiInstrumentDTO selectedInstrument =
                (MidiInstrumentDTO) instrumentComboBox.getSelectedItem();
        if (selectedInstrument == null) {
            messageLabel.setText("No instrument selected.");
            disableAllControls();
            return;
        }

        int noteNumber = (int) noteSpinner.getValue();

        // Attempt to play the note
        try {
            midiService.playNoteAsync(selectedDevice.getId(), selectedInstrument.getId(), noteNumber);
        } catch (Exception e) {
            messageLabel.setText("Error playing note: " + e.getMessage());
            // Keep controls enabled or not depending on how you handle error
        }
    }

    /**
     * Disables the spinner, instrument combo, and play button (keep device combo if you want).
     */
    private void disableAllControls() {
        instrumentComboBox.setEnabled(false);
        noteSpinner.setEnabled(false);
        playButton.setEnabled(false);
    }

    /**
     * Enables controls if everything is valid
     */
    private void enableAllControls() {
        instrumentComboBox.setEnabled(true);
        noteSpinner.setEnabled(true);
        playButton.setEnabled(true);
    }

    /**
     * DEMO MAIN
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MIDI Panel Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);

            MidiDeviceTestPanel midiPanel = new MidiDeviceTestPanel();
            frame.add(midiPanel, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public String getTabName() {
        return "plugin.test.midi.device.title";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public ExtensionCategory getExtensionCategory() {
        return ExtensionCategory.SOUND;
    }

    @Override
    public String getExtensionDescription() {
        return null;
    }
}
