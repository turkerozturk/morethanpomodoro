package com.turkerozturk.buttons;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.turkerozturk.initial.ConfigManager;
import com.turkerozturk.initial.LanguageManager;
import com.turkerozturk.initial.jpanels.sound.controller.SoundController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.turkerozturk.ApplicationFrame.soundControllers;

public class MuteAllButton extends JToggleButton {

    //boolean isSet;

    int iconWidth, iconHeight;

    LanguageManager bundle = LanguageManager.getInstance();
    ConfigManager props = ConfigManager.getInstance();

    private static boolean isGlobalMuted;

    private void toggleGlobalMute() {
        isGlobalMuted = !isGlobalMuted;
        prepareGlobalMute();
    }


    private static final List<Boolean> previousMuteStates = new ArrayList<>();

    public MuteAllButton() {

        super("");
        setFocusable(false);
        setBackground(Color.white);


        isGlobalMuted = Integer.parseInt(props.getProperty("gui.is.mute.all")) == 1;
        iconWidth = Integer.parseInt(props.getProperty("gui.icon.width"));
        iconHeight = Integer.parseInt(props.getProperty("gui.icon.height"));

        prepareGlobalMute();
        this.addActionListener(e -> toggleGlobalMute());


    }




    private void prepareGlobalMute() {
        this.setSelected(isGlobalMuted);

        if (isGlobalMuted) {


            // Global Mute
            previousMuteStates.clear();
            for (SoundController controller : soundControllers) {
                previousMuteStates.add(controller.isMuted());
                controller.mute();
            }
            //isGlobalMuted = true;
            this.setToolTipText("UnMute All");
            FlatSVGIcon globalMuteIcon = new FlatSVGIcon("svg/sound_off__tabler__volume_mute.svg", iconWidth, iconHeight);
            this.setIcon(globalMuteIcon);
            this.setText("");
        } else {
            // Global Unmute
            for (int i = 0; i < soundControllers.size(); i++) {
                if (!previousMuteStates.get(i)) { // Eski durumu kontrol et
                    soundControllers.get(i).unmute();
                }
                //System.out.println( soundControllers.size());
            }
            //isGlobalMuted = false;
            this.setToolTipText("Mute All");
            FlatSVGIcon globalMuteIcon = new FlatSVGIcon("svg/sound_on__tabler__volume_up.svg", iconWidth, iconHeight);
            this.setIcon(globalMuteIcon);
            this.setText("");
            //System.out.println(this.prepareGlobalSoundReport());
        }
    }

    /**
     * test etmek icin kullanacagin zaman icini doldurursun.
     * @return
     */
    private String prepareGlobalSoundReport() {
        StringBuilder sb = new StringBuilder();


        return sb.toString();
    }


    /**
     * bunu sekme uzerinde ikona tiklama ornegi olarak kullanmistim, su an kullanmiyorum
     */
    private void toggleGlobalMuteAtTab() {
        if (isGlobalMuted) {
            // Global Unmute
            for (int i = 0; i < soundControllers.size(); i++) {
                if (!previousMuteStates.get(i)) { // Eski durumu kontrol et
                    soundControllers.get(i).unmute();
                }
            }
            isGlobalMuted = false;
            //muteButtonAtTab.setText("Global Mute");
          //  muteButtonAtTab.setText("🔊"); // 🔊 Ses Açık, 🔇 Ses Kapalı

            System.out.println(this.prepareGlobalSoundReport());
        } else {
            // Global Mute
            previousMuteStates.clear();
            for (SoundController controller : soundControllers) {
                previousMuteStates.add(controller.isMuted());
                controller.mute();
            }
            isGlobalMuted = true;
            //muteButtonAtTab.setText("Global Unmute");
          //  muteButtonAtTab.setText("🔇"); // 🔊 Ses Açık, 🔇 Ses Kapalı

        }
    }








}
