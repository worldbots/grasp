package ru.spb.hibissscus.hotkey;

import java.awt.event.KeyEvent;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * Специальный класс базирующийся на JIntellitype библиотеке позволяющий
 * регистрировать и удалять хоткеи в среде windows
 */
public final class HotKeyController {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(HotKeyController.class);

    private static HotKeyController instance;

    public static HotKeyController getInstance() {
        if (instance == null) {
            instance = new HotKeyController();
        }
        return instance;
    }

    private HotKeyController() {
        initJIntellitype();
    }

    /**
     * Initialize the JInitellitype library making sure the DLL is located.
     */
    public void initJIntellitype() {
        try {
            // Check to make sure JIntellitype DLL can be found and we are on
            // a Windows operating System, if all ok start JIntellitype
            if (!JIntellitype.isJIntellitypeSupported()) {
                LOG.error("JIntellitype.DLL is not found in the path or this is not Windows OS.");
                System.exit(1);
            }

            LOG.info("JIntellitype initialized");
        } catch (RuntimeException ex) {
            LOG.error("Either you are not on Windows, or there is a problem with the JIntellitype library!");
            LOG.error(ex.getMessage());
        }
    }

    /**
     * Добавляем новое действие по сочетанию клавиш
     * 
     * @param modifier
     *            MOD_SHIFT, MOD_ALT, MOD_CONTROL, MOD_WIN from
     *            JIntellitypeConstants, or 0 if no modifier needed
     * @param keyEvent
     *            the key
     * @param action
     *            action by this keys combination
     */
    public void addActionByHotkey(int modifier, KeyEvent keyEvent,
            final Action action) {
        addActionByHotkey(modifier, keyEvent, action);
    }

    /**
     * Добавляем новое действие по сочетанию клавиш
     * 
     * @param modifier
     *            MOD_SHIFT, MOD_ALT, MOD_CONTROL, MOD_WIN from
     *            JIntellitypeConstants, or 0 if no modifier needed
     * @param keycode
     *            the key to respond to in Ascii integer, 65 for A
     * @param action
     *            action by this keys combination
     */
    public void addActionByHotkey(int modifier, int keycode, final Action action) {
        int hashkey = buildHashByKeycode(modifier, keycode);

        JIntellitype.getInstance().registerHotKey(hashkey, modifier, keycode);
        JIntellitype.getInstance().addHotKeyListener(
                new ExtendedHotkeyListener(hashkey, action));
    }

    public static int buildHashByKeycode(int modifier, int keycode) {
        int hashkey = 0;
        hashkey = 31 * hashkey + modifier;
        hashkey = 31 * hashkey + keycode;
        return hashkey;
    }

    /**
     * Реализация HotkeyListener с возможностью контроля ключа, по которому
     * выполняется действие
     */
    private class ExtendedHotkeyListener implements HotkeyListener {

        private Action action;

        private int key;

        private ExtendedHotkeyListener(final int key, final Action action) {
            this.action = action;
            this.key = key;
        }

        @Override
        public void onHotKey(int identifier) {
            if (identifier == key) {
                LOG.info("Button: " + KeyEvent.getKeyModifiersText(key));
                action.actionPerformed(null);
            }
        }
    }
}
