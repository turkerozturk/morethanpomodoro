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
package org.example.jpanels.pomodoro;

import org.example.AnotherNoiseGenerator;
import org.example.initial.ConfigManager;
import org.example.initial.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroService {

    private static final Logger logger = LoggerFactory.getLogger(PomodoroService.class);

    // Zamanlayıcı tipleri
    public enum PomodoroTimerType {
        WORK_TIME, SHORT_BREAK, LONG_BREAK;
    }

    // Zamanlayıcı durumları
    public enum PomodoroTimerStatus {
        STOPPED, PAUSED, PLAYING;
    }

    AnotherNoiseGenerator audioSynthesizer;

    // Süreler dakika cinsinden; GUI üzerinden spinner ile ayarlanır.
    private int workDurationMinutes;
    private int shortBreakDurationMinutes;
    private int longBreakDurationMinutes;

    // Toplam pomodoro sayısı (örneğin 4) ve mevcut çalışma seansı sayısı.
    private int totalWorkSessions;
    private int currentWorkSession = 1; // ilk çalışma seansı

    // Aktif zamanlayıcı bilgileri
    private PomodoroTimerType activeTimerType = PomodoroTimerType.WORK_TIME;
    private PomodoroTimerStatus timerStatus = PomodoroTimerStatus.STOPPED;

    // start tick sound related code
    private List<TimerTickListener> listeners = new ArrayList<>();

    public void addTimerTickListener(TimerTickListener listener) {
        listeners.add(listener);
    }

    private void notifyTick() {
        for (TimerTickListener listener : listeners) {
            listener.onTick(remainingSeconds);
        }
    }
    // end tick sound related code

    // start ending sound related code
    private List<TimerFinishedListener> finishedListeners = new ArrayList<>();

    public void addTimerFinishedListener(TimerFinishedListener listener) {
        finishedListeners.add(listener);
    }

    private void notifyTimerFinished() {
        for (TimerFinishedListener listener : finishedListeners) {
            listener.onTimerFinished();
        }
    }
    // end ending sound related code


    // Kalan süre (saniye cinsinden)
    private int remainingSeconds;

    // Otomatik oynatma (auto play) özelliği
    private boolean autoPlayEnabled;

    // Timer nesnesi
    private Timer timer;
    private TimerTask currentTask;

    private final LanguageManager bundle = LanguageManager.getInstance();
    private final ConfigManager props = ConfigManager.getInstance();

    public PomodoroService() {
        workDurationMinutes = Integer.parseInt(props.getProperty("pomodoro.timer.work.duration"));
        shortBreakDurationMinutes = Integer.parseInt(props.getProperty("pomodoro.timer.short.break"));
        longBreakDurationMinutes = Integer.parseInt(props.getProperty("pomodoro.timer.long.break"));
        totalWorkSessions = Integer.parseInt(props.getProperty("pomodoro.timer.cycle.count"));
        autoPlayEnabled = Integer.parseInt(props.getProperty("pomodoro.timer.autoplay.is.enabled")) == 1;
        /*
        logger.info(String.format("%s %s %s %s %s",workDurationMinutes,
                shortBreakDurationMinutes,
                longBreakDurationMinutes,
                totalWorkSessions,
                autoPlayEnabled));
        */
        // İlk başta aktif zamanlayıcıya ait süreyi ayarla.
        reset();
    }

    // remainingSeconds getter ve setter
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    // Spinner'lar tarafından çağrılabilecek; ilgili süre değerlerini güncelleme metotları.
    public void setWorkDurationMinutes(int minutes) {
        this.workDurationMinutes = minutes;
        if(activeTimerType == PomodoroTimerType.WORK_TIME) {
            reset(); // aktif timer ise kalan süreyi güncelle.
        }
    }

    public void setShortBreakDurationMinutes(int minutes) {
        this.shortBreakDurationMinutes = minutes;
        if(activeTimerType == PomodoroTimerType.SHORT_BREAK) {
            reset();
        }
    }

    public void setLongBreakDurationMinutes(int minutes) {
        this.longBreakDurationMinutes = minutes;
        if(activeTimerType == PomodoroTimerType.LONG_BREAK) {
            reset();
        }
    }

    // Aktif zamanlayıcı türüne göre başlangıç saniyelerini döner.
    private int getActiveTimerInitialSeconds() {
        switch(activeTimerType) {
            case WORK_TIME:
                return workDurationMinutes * 60;
            case SHORT_BREAK:
                return shortBreakDurationMinutes * 60;
            case LONG_BREAK:
                return longBreakDurationMinutes * 60;
            default:
                return 0;
        }
    }

    // Zamanlayıcıyı başlatır.
    public void start() {
        if(timerStatus == PomodoroTimerStatus.PLAYING) return; // zaten oynuyor

        timerStatus = PomodoroTimerStatus.PLAYING;

        // Eğer kalan süre 0 ise resetle
        if(remainingSeconds <= 0) {
            reset();
        }

        if(getActiveTimerType().equals(PomodoroTimerType.SHORT_BREAK)) {
            audioSynthesizer = new AnotherNoiseGenerator();
            audioSynthesizer.play((int) shortBreakDurationMinutes * 60 * 1000);
        }

        timer = new Timer();
        currentTask = new TimerTask() {
            @Override
            public void run() {
                remainingSeconds--;
                notifyTick(); // GUI güncellenebilir
                if(remainingSeconds <= 0) {
                    stop();
                    notifyTimerFinished();
                    if(autoPlayEnabled) {
                        next();
                        start();
                    }
                }
            }
        };
        // Her saniye çalıştır
        timer.scheduleAtFixedRate(currentTask, 1000, 1000);
    }

    // Zamanlayıcıyı durdurur.
    public void stop() {
        timerStatus = PomodoroTimerStatus.STOPPED;
        if(currentTask != null) {
            currentTask.cancel();
        }
        if(timer != null) {
            timer.cancel();
        }
        if(audioSynthesizer != null) {
            audioSynthesizer.stop();
        }
    }

    // Aktif zamanlayıcıyı başlangıç değerine resetler.
    public void reset() {
        stop();
        remainingSeconds = getActiveTimerInitialSeconds();
        timerStatus = PomodoroTimerStatus.STOPPED;
    }

    // Pomodoro döngüsünde bir sonraki zamanlayıcıya geçiş yapar.
    public void next() {
        stop();
        if(activeTimerType == PomodoroTimerType.WORK_TIME) {
            // Eğer son çalışma seansı ise, uzun molaya geç.
            if(currentWorkSession >= totalWorkSessions) {
                activeTimerType = PomodoroTimerType.LONG_BREAK;
            } else {
                activeTimerType = PomodoroTimerType.SHORT_BREAK;
            }
        } else {
            // Moladan sonra her zaman çalışma süresine geç.
            // Eğer önceki zamanlayıcı WORK_TIME bitmiş ise, zaten currentWorkSession arttırılmış olabilir.
            // Burada, SHORT_BREAK veya LONG_BREAK bittikten sonra yeni bir pomodoro başlıyor.
            // Yeni pomodoro başlamadan önce, eğer uzun mola ise resetleyip sayacı sıfırlayabilirsiniz.
            if(activeTimerType == PomodoroTimerType.LONG_BREAK) {
                // Döngüyü yeniden başlatmak istiyorsak
                currentWorkSession = 1;
            } else {
                currentWorkSession++; // SHORT_BREAK sonrası yeni çalışma seansı
            }
            activeTimerType = PomodoroTimerType.WORK_TIME;
        }
        reset();
    }

    // Otomatik oynatma özelliğini etkinleştirir veya devre dışı bırakır.
    public void enableAutoPlay(boolean enabled) {
        this.autoPlayEnabled = enabled;
    }

    // "pomodoro X of Y" bilgisini döner.
    public String getCurrentWorkSession() {
        return "Pomodoro " + currentWorkSession;
        //return "Pomodoro " + currentWorkSession + " of " + totalWorkSessions;
    }

    // Aktif timer'ın türünü döner.
    public PomodoroTimerType getActiveTimerType() {
        return activeTimerType;
    }

    /*
    // GUI spinner'ından aktif timer süresi değiştirildiğinde çağrılabilir.
    // Yeni dakika değerine göre kalan süreyi hemen günceller.
    public void updateActiveTimerDuration(int newMinutes) {
        switch(activeTimerType) {
            case WORK_TIME:
                workDurationMinutes = newMinutes;
                break;
            case SHORT_BREAK:
                shortBreakDurationMinutes = newMinutes;
                break;
            case LONG_BREAK:
                longBreakDurationMinutes = newMinutes;
                break;
        }
        // Eski başlangıç süresine göre farkı alıp kalan saniyeyi güncelleyelim.
        // Örneğin; spinner değeri 2 dakika artmışsa kalan saniyeye 120 eklenir.
        int newInitialSeconds = newMinutes * 60;
        int oldInitialSeconds = getActiveTimerInitialSeconds();
        int delta = newInitialSeconds - oldInitialSeconds;
        remainingSeconds += delta;
        // Eğer reset yapılırsa, kesin olarak yeni değeri alır.
    }
    */

    // PomodoroService.java içinde
    public void updateActiveTimerDuration(int newMinutes) {
        // Önce, eski (aktif) süreyi (dakika cinsinden) alalım.
        int oldMinutes = 0;
        switch(activeTimerType) {
            case WORK_TIME:
                oldMinutes = workDurationMinutes;
                break;
            case SHORT_BREAK:
                oldMinutes = shortBreakDurationMinutes;
                break;
            case LONG_BREAK:
                oldMinutes = longBreakDurationMinutes;
                break;
        }

        // Eski başlangıç saniyelerini ve geçen süreyi hesaplayalım.
        int oldInitialSeconds = oldMinutes * 60;
        int elapsed = oldInitialSeconds - remainingSeconds;

        // Şimdi yeni dakika değerini ilgili alana aktaralım.
        switch(activeTimerType) {
            case WORK_TIME:
                workDurationMinutes = newMinutes;
                break;
            case SHORT_BREAK:
                shortBreakDurationMinutes = newMinutes;
                break;
            case LONG_BREAK:
                longBreakDurationMinutes = newMinutes;
                break;
        }

        // Yeni başlangıç saniyelerini alalım.
        int newInitialSeconds = newMinutes * 60;

        // Kalan süreyi, o ana kadar geçen süreyi koruyarak güncelleyelim.
        remainingSeconds = Math.max(0, newInitialSeconds - elapsed);
    }





    // Getters for durations (dakika cinsinden)
    public int getWorkDurationMinutes() {
        return workDurationMinutes;
    }

    public int getShortBreakDurationMinutes() {
        return shortBreakDurationMinutes;
    }

    public int getLongBreakDurationMinutes() {
        return longBreakDurationMinutes;
    }

    // Timer durumunu döner.
    public PomodoroTimerStatus getTimerStatus() {
        return timerStatus;
    }

    // Toplam pomodoro sayısını güncellemek için (örn. spinner'da ayarlanabilir)
    public void setTotalWorkSessions(int totalSessions) {
        this.totalWorkSessions = totalSessions;
    }

    public int getTotalWorkSessions() {
        return totalWorkSessions;
    }

    public boolean isAutoPlayEnabled() {
        return autoPlayEnabled;
    }

    public void setAutoPlayEnabled(boolean autoPlayEnabled) {
        this.autoPlayEnabled = autoPlayEnabled;
    }
}
