package org.example.plugin;

import javax.swing.*;

/* bilgi: her plugin projesinde src/main/resources/META-INF/services/ klasoru olusturacaksin.
ve klasorun icin bu interface dosyasinin buradaki tam yoluile ayni adda
 yani org.example.plugin.PanelPlugin adinda bir dosya olusturacaksin. Yani asagidaki gibi:
src/main/resources/META-INF/services/org.example.plugin.PanelPlugin
Ve olusturdugun dosyanin icine o plugin projesinde bu interface i kullanan sinif veya siniflarin
isimlerini ekleyeceksin, mesela: org.example.CountdownTimerPanel yazarsan artik ana uygulama tarafindan
plugin uygulmasindaki CountdownTimerPanel sinifi kullanilabilir olur.
 */
public interface PanelPlugin {
    String getTabName();         // Sekme başlığını döndür
    JPanel getPanel();           // Eklenti panelini döndür
}
