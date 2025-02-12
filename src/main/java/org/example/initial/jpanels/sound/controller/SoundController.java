package org.example.initial.jpanels.sound.controller;

/**
 * Tüm ses bileşenlerinin implement etmesi gereken arayüz.
 */
public interface SoundController {
    void mute();
    void unmute();
    boolean isMuted();
}