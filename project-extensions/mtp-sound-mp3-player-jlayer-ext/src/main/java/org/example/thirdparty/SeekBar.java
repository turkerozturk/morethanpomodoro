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
package org.example.thirdparty;
/*
 * This file is copied from the Musicplayer project
 * (https://github.com/Velliz/Musicplayer), specifically from the commit:
 * https://github.com/Velliz/Musicplayer/commit/41c4c5ee21a21a845865c34c2b847c75d3349604
 *
 * The original code is licensed under the Apache License Version 2.0,
 * January 2004 (http://www.apache.org/licenses/).
 *
 * As per the requirements of the Apache License, the original copyright
 * notice, license text, and disclaimer must be retained:
 *
 *  --- Begin Apache License Notice ---
 *  Copyright (c) 2017 Velliz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  --- End Apache License Notice ---
 */
import javazoom.jlgui.basicplayer.BasicPlayerException;
import org.example.thirdparty.utils.BackgroundExecutor;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SeekBar extends JProgressBar {

    AudioPlayer p = AudioPlayer.getInstance();

    //private int lastSeekVal = 0; //relative to the seekbar value
    //private int lastSeekSec = 0; //relative to the player value (passed sec)
    //This value in necessary because BasicPlayer resets it's returned progress
    //each time we use seek. The value should reset when we open a new song
    private int updatedValue = 0; //sharing between different scopes

    /**
     * Update SeekBar position
     *
     * @param progress in microseconds
     * @param totalVal in seconds
     */
    public void updateSeekBar(long progress, float totalVal) {
        if (p.isSeeking())
            return;
        BackgroundExecutor.get().execute(new UpdatingTask(progress, totalVal));
        setValue(updatedValue);
        //log("Seek val : "+n + " " + lp +" t:" + totalVal + " sl:" + seekLenght);
    }

    /**
     * Task used for updating the seek value in another thread.
     *
     * @author Pierluigi
     */
    private class UpdatingTask implements Runnable {

        long progress;
        float totalVal;

        public UpdatingTask(long progress, float totalVal) {
            this.progress = progress;
            this.totalVal = totalVal;
        }

        @Override
        public void run() {

            int lp = (int) (progress / 1000); //progress comes in microseconds
            int seekLenght = getMaximum();
            int n = (int) ((lp / (totalVal * 1000)) * seekLenght);
            updatedValue = n;
        }
    }
    ///////////////////////////////////////////////////////////

    /**
     * New Constructor, sets a mouseListener
     * (extends JProgressBar)
     */
    public SeekBar() {
        super();
        setMaximum(10000);
        addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (p.isSeeking())
                    return;
                float val = ((float) e.getX() / getWidth()) * getMaximum();
                returnValueToPlayer(val);
                setValue((int) val);
                log("SeekBar pressed: " + val + " x: " + e.getX());

            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    /**
     * Informs the player about the relative value selected in the seekbar
     *
     * @throws BasicPlayerException
     */
    private void returnValueToPlayer(float val) {
        BackgroundExecutor.get().execute(new SeekTask(val));
    }

    /**
     * Class for executing seek outside the UI thread
     *
     * @author Pierluigi
     */
    class SeekTask implements Runnable {

        float val;

        public SeekTask(float val) {
            this.val = val;
        }

        @Override
        public void run() {
            //AudioPlayer p = AudioPlayer.getInstance(); //Static call using singleton
            float relVal = val / getMaximum();
            int newSongPos = (int) (relVal * p.getAudioDurationSeconds());
            //lastSeekVal = getValue();
            //lastSeekSec = newSongPos; //Given the position in microseconds
            p.setLastSeekPositionInMs(newSongPos * 1000000);
            //what's the seek value in bytes?
            long seekvalue = (long) ((float) newSongPos * p.getAudioFrameRate() * p.getAudioFrameSize());
            try {
                p.seek(seekvalue);
            } catch (BasicPlayerException e) {
                log("Error with calculated seek value");
                e.printStackTrace();
            }
        }
    }
/////////////////////////
    /*
	public void resetLastSeek()
	{
		lastSeekVal = 0;
		lastSeekSec = 0;
	}*/

    /**
     * The returned value refears to seconds
     *
     * @return
     */
	/*
	public int getLastSeekSec()
	{
		return lastSeekSec;
	}*/
    private void log(String str) {
        System.out.println("SeekBar] " + str);
    }
}

