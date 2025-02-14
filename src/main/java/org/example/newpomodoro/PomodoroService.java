package org.example.newpomodoro;

import java.util.Timer;
import java.util.TimerTask;

public class PomodoroService {

    // Zamanlayıcı tipleri
    public enum PomodoroTimerType {
        WORK_TIME, SHORT_BREAK, LONG_BREAK;
    }

    // Zamanlayıcı durumları
    public enum PomodoroTimerStatus {
        STOPPED, PAUSED, PLAYING;
    }

    // Süreler dakika cinsinden; GUI üzerinden spinner ile ayarlanır.
    private int workDurationMinutes = 25;
    private int shortBreakDurationMinutes = 5;
    private int longBreakDurationMinutes = 15;

    // Toplam pomodoro sayısı (örneğin 4) ve mevcut çalışma seansı sayısı.
    private int totalWorkSessions = 4;
    private int currentWorkSession = 1; // ilk çalışma seansı

    // Aktif zamanlayıcı bilgileri
    private PomodoroTimerType activeTimerType = PomodoroTimerType.WORK_TIME;
    private PomodoroTimerStatus timerStatus = PomodoroTimerStatus.STOPPED;

    // Kalan süre (saniye cinsinden)
    private int remainingSeconds;

    // Otomatik oynatma (auto play) özelliği
    private boolean autoPlayEnabled = false;

    // Timer nesnesi
    private Timer timer;
    private TimerTask currentTask;

    public PomodoroService() {
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

        timer = new Timer();
        currentTask = new TimerTask() {
            @Override
            public void run() {
                remainingSeconds--;
                // Burada GUI tarafına update sinyali gönderilebilir (observer pattern vb.)
                if(remainingSeconds <= 0) {
                    stop(); // zamanlayıcıyı durdur
                    // Zamanlayıcı bittiğinde otomatik geçiş varsa uygulansın
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
        return "pomodoro " + currentWorkSession + " of " + totalWorkSessions;
    }

    // Aktif timer'ın türünü döner.
    public PomodoroTimerType getActiveTimerType() {
        return activeTimerType;
    }

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
}
