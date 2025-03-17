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
package com.turkerozturk.basicplayertest;

import javazoom.jlgui.basicplayer.*;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

/**
 * This class implements a simple player based on BasicPlayer.
 * BasicPlayer is a threaded class providing most features
 * of a music player. BasicPlayer works with underlying JavaSound
 * SPIs to support multiple audio formats. Basically JavaSound supports
 * WAV, AU, AIFF audio formats. Add MP3 SPI and Vorbis
 * SPI in your CLASSPATH to play MP3 and Ogg Vorbis file.
 */
public class BasicPlayerTest implements BasicPlayerListener {
    private PrintStream out = null;

    /**
     * Entry point.
     *
     * @param args filename to play.
     */
    public static void main(String[] args) {
        BasicPlayerTest test = new BasicPlayerTest();
        test.play(args[0]);
        //test.play("C:\\tmp\\test.mp3");

    }

    /**
     * Contructor.
     */
    public BasicPlayerTest() {
        out = System.out;
    }

    public void play(String filename) {
        // Instantiate BasicPlayer.
        BasicPlayer player = new BasicPlayer();
        // BasicPlayer is a BasicController.
        BasicController control = (BasicController) player;
        // Register BasicPlayerTest to BasicPlayerListener events.
        // It means that this object will be notified on BasicPlayer
        // events such as : opened(...), progress(...), stateUpdated(...)
        player.addBasicPlayerListener(this);

        try {
            // Open file, or URL or Stream (shoutcast, icecast) to play.
            control.open(new File(filename));

            // control.open(new URL("http://yourshoutcastserver.com:8000"));

            // Start playback in a thread.
            control.play();

            // If you want to pause/resume/pause the played file then
            // write a Swing player and just call control.pause(),
            // control.resume() or control.stop().
            // Use control.seek(bytesToSkip) to seek file
            // (i.e. fast forward and rewind). seek feature will
            // work only if underlying JavaSound SPI implements
            // skip(...). True for MP3SPI and SUN SPI's
            // (WAVE, AU, AIFF).

            // Set Volume (0 to 1.0).
            control.setGain(0.85);
            // Set Pan (-1.0 to 1.0).
            control.setPan(0.0);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open callback, stream is ready to play.
     * <p>
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag, ...
     *
     * @param stream     could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    public void opened(Object stream, Map properties) {
        // Pay attention to properties. It's useful to get duration,
        // bitrate, channels, even tag such as ID3v2.
        display("opened : " + properties.toString());
    }

    /**
     * Progress callback while playing.
     * <p>
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     *
     * @param bytesread    from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata      PCM samples.
     * @param properties   audio stream parameters.
     */
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        // Pay attention to properties. It depends on underlying JavaSound SPI
        // MP3SPI provides mp3.equalizer.
        display("progress : " + properties.toString());
    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *
     * @param event
     */
    public void stateUpdated(BasicPlayerEvent event) {
        // Notification of BasicPlayer states (opened, playing, end of media, ...)
        display("stateUpdated : " + event.toString());
    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     *
     * @param controller : a handle to the player
     */
    public void setController(BasicController controller) {
        display("setController : " + controller);
    }

    public void display(String msg) {
        if (out != null) out.println(msg);
    }
}