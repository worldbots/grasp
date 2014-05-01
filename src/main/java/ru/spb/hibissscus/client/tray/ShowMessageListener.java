package ru.spb.hibissscus.client.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Special ActionListener for System Tray
 */
class ShowMessageListener implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ShowMessageListener.class);

    private TrayIcon trayIcon;
    private String title;
    private String message;
    private TrayIcon.MessageType messageType;

    ShowMessageListener(TrayIcon trayIcon, String title, String message, TrayIcon.MessageType messageType) {
        this.trayIcon = trayIcon;
        this.title = title;
        this.message = message;
        this.messageType = messageType;
    }

    public void actionPerformed(ActionEvent e) {
        trayIcon.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.debug("Message Clicked");
            }
        });
        trayIcon.displayMessage(title, message, messageType);
    }
}
