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

public enum PlayerEvent {
    UNKNOWN("player.event.unknown", -1),
    OPENING("player.event.opening", 0),
    OPENED("player.event.opened", 1),
    PLAYING("player.event.playing", 2),
    STOPPED("player.event.stopped", 3),
    PAUSED("player.event.paused", 4),
    RESUMED("player.event.resumed", 5),
    SEEKING("player.event.seeking", 6),
    SEEKED("player.event.seeked", 7),
    EOM("player.event.eom", 8),
    PAN("player.event.pan", 9),
    GAIN("player.event.gain", 10);

    private final String description;
    private final int eventId;

    PlayerEvent(String description, int eventId) {
        this.description = description;
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getDescription() {
        return description;
    }

    public static PlayerEvent fromEventId(int eventId) {
        for (PlayerEvent event : values()) {
            if (event.eventId == eventId) {
                return event;
            }
        }
        return UNKNOWN;
    }
}
