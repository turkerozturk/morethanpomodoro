package org.example;

import java.util.Random;

public class TickSoundSystem {
    private int counter = 0;
    private int tickInterval = 5;  // Örnek bir tick aralığı
    private boolean isMuted = false;
    private boolean isRandomEnabled = false;

    private int remainingTicksToPlay = 0;
    private int remainingTicksToMute = 0;
    private final Random random = new Random();

    public TickSoundSystem(int tickInterval, boolean isMuted, boolean isRandomEnabled) {
        this.tickInterval = tickInterval;
        this.isMuted = isMuted;
        this.isRandomEnabled = isRandomEnabled;
        resetRandomTicks();
    }

    public void tick() {
        counter++;

        if (counter >= tickInterval) {
            if (!isMuted) {
                if (isRandomEnabled) {
                    if (remainingTicksToMute > 0) {
                        remainingTicksToMute--;
                    } else {
                        playSound();
                        remainingTicksToPlay--;

                        // Eğer çalma süresi bittiyse, rastgele yeni bir süre belirle
                        if (remainingTicksToPlay <= 0) {
                            resetRandomTicks();
                        }
                    }
                } else {
                    playSound();
                }
            }
            counter = 0;
        }
    }

    private void resetRandomTicks() {
        remainingTicksToPlay = random.nextInt(10) + 1; // 1 ile 10 arasında rastgele çalma süresi
        remainingTicksToMute = random.nextInt(4) + 1;  // 1 ile 4 arasında rastgele sessizlik süresi
    }

    private void playSound() {
        System.out.println("Beep!");  // Buraya gerçek ses çalma kodu eklenebilir.
    }

    public static void main(String[] args) throws InterruptedException {
        TickSoundSystem ticker = new TickSoundSystem(5, false, true);

        // Simülasyon: 50 tick boyunca çalıştır
        for (int i = 0; i < 50; i++) {
            ticker.tick();
            Thread.sleep(100);  // Tick hızını simüle etmek için 100ms bekle
        }
    }
}
