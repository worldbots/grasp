package ru.spb.hibissscus.common;

import javax.swing.*;

/**
 * Nimbus LookAndFeel
 */
public class Nimbus {

    public static void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            UIManager.put("Table.showGrid", "true");
        } catch (Exception e) {
            e.printStackTrace();
            // If Nimbus is not available, you can set the GUI to another look
            // and feel.
        }
    }

}
