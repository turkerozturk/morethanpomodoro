// Özel paste devre dışı bırakılan metin alanı
package org.example.jpanels.configuration;

import javax.swing.*;
import java.awt.*;

public class NoPasteTextField extends JTextField {
    public NoPasteTextField(int columns) {
        super(columns);
    }

    @Override
    public void paste() {
        // Paste işlemini devre dışı bırakıyoruz
        Toolkit.getDefaultToolkit().beep(); // Kullanıcıya uyarı sesi verir.
        System.out.println("Paste işlemi devre dışı bırakıldı.");
    }
}
