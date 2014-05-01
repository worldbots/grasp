package ru.spb.hibissscus.client;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import javax.swing.*;

import org.apache.http.HttpHost;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.hibissscus.client.tray.TrayClient;
import ru.spb.hibissscus.common.LanguageType;
import ru.spb.hibissscus.common.Nimbus;
import ru.spb.hibissscus.common.ProxyUtil;
import ru.spb.hibissscus.common.Settings;
import ru.spb.hibissscus.hotkey.HotKeyController;
import ru.spb.hibissscus.http.GoogleTranslationService;
import ru.spb.hibissscus.sound.SoundMaster;

import com.melloware.jintellitype.JIntellitype;

/**
 * Entry point.
 */
public final class MainController {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(MainController.class);

    /**
     * Default proxy
     */
    private static final HttpHost DEFAULT_PROXY = new HttpHost("10.206.247.66", 8080);

    /**
     * Setup new LookAndFeel
     */
    static {
        try {
            Nimbus.setNimbusLookAndFeel();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Tray component to make it possible to interact with the application via
     * system tray
     */
    private TrayClient trayClient = new TrayClient();

    /**
     * Service to make the translation and voices
     */
    private GoogleTranslationService google;

    /**
     * Special balloon to show the text in the variable positions on the screen
     */
    private BalloonWorker balloonWorker;

    /**
     * Constructor.
     */
    private MainController() {
        google = new GoogleTranslationService();
        // DEFAULT_PROXY = getProxy();
    }

    /**
     * Auto return proxy   //TODO refactoring
     */
    private HttpHost getProxy() {
        try {
            final String PAC_URL = "http://pac.t-systems.com:3132/proxy.pac";

            URL url = new URL(PAC_URL);
            Proxy proxy = ProxyUtil.getProxy(url, new URL(
                    "http://www.google.com"));
            LOG.debug("proxy: {}" + proxy);
            return new HttpHost("10.206.247.66", 8080);

        } catch (MalformedURLException e) {
            LOG.error("{}", e);
            return null;
        }
    }

    /**
     * Some default settings
     */
    private void defaultAction() {
        checkProxy();
    }

    /**
     * Quickly google translation
     * <p/>
     * example: Anhoehen, für, fünf fuer, コンサルティング
     */
    private Action translation = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            defaultAction();

            Settings settings = getTrayClient().getSettings();

            boolean translation = settings.isTranslate();
            boolean voice = settings.isVoice();
            boolean voiceTo = settings.isVoiceTo();
            LanguageType from = settings.getFromLanguage();
            LanguageType to = settings.getToLanguage();

            google.setInputText(getClipboard());

            // if (voice && translation) {
            // // google.makeTranslationsAndVoices(from, to);
            // } else
            if (translation) {
                google.makeTranslations(from, to);
            }

            if (voice) {

                // try to voice Translation in target language
                if (voiceTo) {
                    google.makeTextVoices(google.getTranslations(), to);
                } else {
                    if (LanguageType.AUTODETECTED == from)
                        from = google.getLastTranslation().getFromLanguage();
                    google.makeVoices(from);
                }
            }

            try {
                // should we use tray translation?
                if (translation) {
                    getTrayClient().showText("", google.getTranslations());
                }
                // should we use voice?
                if (voice) {
                    SoundMaster.getInstance()
                            .playAllRecords(google.getVoices());
                }

            } catch (IOException er) {
                er.printStackTrace();
            }
        }
    };

    /**
     * Only voice the text in clipboard
     */
    private Action voiceOnly = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {

            Settings settings = getTrayClient().getSettings();

            // should we use voice?
            if (settings.isVoice()) {
                defaultAction();
                google.setInputText(getClipboard());

                LanguageType from = settings.getFromLanguage();
                LanguageType to = settings.getToLanguage();

                if (!getTrayClient().getSettings().isVoiceTo()) {
                    google.makeVoices(from);
                } else {
                    google.makeTextVoices(
                            google.getTranslations(), to);
                }

                try {
                    SoundMaster.getInstance()
                            .playAllRecords(google.getVoices());
                } catch (IOException er) {
                    er.printStackTrace();
                }

            }
        }
    };

    /**
     * Translate word, which cover by mouse.
     */
    private Action mouseTranslate = new AbstractAction() {

        /**
         * Keyboard and mouse awt robot, to simulate button press
         */
        private Robot robot;

        /**
         * Spetial dalay for robot
         */
        private static final int ROBOT_DALAY = 70;

        {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                LOG.error(e.getMessage());
            }
        }

        /**
         * KeyKode for <VK_WINDOWS + VK_C>
         */
        final int keyCode = HotKeyController.buildHashByKeycode(
                JIntellitype.MOD_WIN, KeyEvent.VK_C);

        @Override
        public void actionPerformed(final ActionEvent e) {

            // UnregisterHotKey VK_CAPS_LOCK
            JIntellitype.getInstance().unregisterHotKey(keyCode);

            // Get Mouse Location on the screen
            Point mouseLocation = Display.getDefault().getCursorLocation();

            // TODO сделать резервное копирование перед осуществлением
            // копирования

            String oldest = getClipboard();

            // Simulator CTRL+C
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_C);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyRelease(java.awt.event.KeyEvent.VK_C);

            robot.delay(ROBOT_DALAY);

            // Get Clipboard Check data change? then selection
            String str2 = getClipboard();

            // select one word if it possible
            if (oldest.equals(str2)) {
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_MASK);
            }

            robot.delay(ROBOT_DALAY);

            // copy select text on the screen
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_C);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyRelease(java.awt.event.KeyEvent.VK_C);

            robot.delay(ROBOT_DALAY);

            translation.actionPerformed(e);

            // should we use balloon?
            if (getTrayClient().getSettings().isBalloon()) {
                if (balloonWorker != null)
                    balloonWorker.cancel();
                balloonWorker = new BalloonWorker(
                        google.getLastTranslation().getToWord());
                balloonWorker.setPoint(mouseLocation);
                balloonWorker.execute();
            }

            // Deselect button - hide
            // robot.keyPress(KeyEvent.VK_CAPS_LOCK);
            // robot.keyRelease(KeyEvent.VK_CAPS_LOCK);

            JIntellitype.getInstance().registerHotKey(keyCode,
                    JIntellitype.MOD_WIN, KeyEvent.VK_C);
        }

    };

    /**
     * Set up into Clipboard word which we would like to translate
     */
    private Action setFromToClip = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (google.getLastTranslation() != null)
                setClipboardContents(google.getLastTranslation().getFromWord());
        }
    };

    /**
     * Set up into Clipboard translation
     */
    private Action setToToClip = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (google.getLastTranslation() != null)
                setClipboardContents(google.getLastTranslation().getToWord());
        }
    };

    // /**
    // * REST service action
    // */
    // private Action restService = new AbstractAction() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // getTrayClient().showText("", google.getHttpMaster().restAddTranslation(
    // google.getLastTranslation()));
    //
    // }
    // };

    /**
     * Switch languages
     */
    private Action switchLanguages = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            getTrayClient().switchLanguages();
        }
    };

    /**
     * Spin left languages
     */
    private Action spinLeftLanguage = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            getTrayClient().spinLanguages(true);
        }
    };

    /**
     * Spin tight languages
     */
    private Action spinRightLanguage = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            getTrayClient().spinLanguages(false);
        }
    };

    /**
     * We must check proxy settings before we will start communicate with Google
     * TODO! FIXME: add additional interface to setting proxy, remove default
     */
    private void checkProxy() {
        if (getTrayClient().getSettings().isProxy()) {
            google.setProxy(DEFAULT_PROXY);
        } else {
            google.setProxy(null);
        }

    }

    private void initComponents() {

        // HotKeyController.getInstance().addActionByHotkey(JIntellitype.MOD_CONTROL
        // + JIntellitype.MOD_SHIFT,
        // KeyEvent.VK_X, mouseTranslate);

        // HotKeyController.getInstance().addActionByHotkey(JIntellitype.MOD_CONTROL
        // + JIntellitype.MOD_SHIFT,
        // KeyEvent.VK_X, translation);

        // HotKeyController.getInstance().addActionByHotkey(JIntellitype.MOD_CONTROL
        // + JIntellitype.MOD_SHIFT,
        // KeyEvent.VK_C, setFromToClip);
        //
        // HotKeyController.getInstance().addActionByHotkey(JIntellitype.MOD_CONTROL
        // + JIntellitype.MOD_SHIFT,
        // KeyEvent.VK_V, setToToClip);
        //
        //

        HotKeyController.getInstance();

        // make translation by hot key
        HotKeyController.getInstance().addActionByHotkey(
                JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT,
                KeyEvent.VK_C, translation);

        // make translation by mouse target
        HotKeyController.getInstance().addActionByHotkey(JIntellitype.MOD_WIN,
                KeyEvent.VK_C, mouseTranslate);

        // // send translation to web service
        // HotKeyController.getInstance().addActionByHotkey(0, KeyEvent.VK_F11,
        // restService);

        // switch languages
        HotKeyController.getInstance().addActionByHotkey(
                JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT,
                KeyEvent.VK_S, switchLanguages);

        // spin left language
        HotKeyController.getInstance().addActionByHotkey(
                JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT,
                KeyEvent.VK_Z, spinLeftLanguage);

        // spin right language
        HotKeyController.getInstance().addActionByHotkey(
                JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT,
                KeyEvent.VK_X, spinRightLanguage);

        // spin right language
        HotKeyController.getInstance().addActionByHotkey(
                JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT,
                KeyEvent.VK_V, voiceOnly);

    }

    public TrayClient getTrayClient() {
        return trayClient;
    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an empty
     *         String.
     */
    private static String getClipboard() {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        DataFlavor flavor = DataFlavor.stringFlavor;
        if (clipboard.isDataFlavorAvailable(flavor)) {
            try {
                return (String) clipboard.getData(flavor);
            } catch (UnsupportedFlavorException e) {
                LOG.error("UnsupportedFlavorException.");
            } catch (IOException e) {
                LOG.error("IOException.");
            }
        }
        return "";
    }

    // /**
    // * Get the String residing on the clipboard.
    // *
    // * @return any text found on the Clipboard; if none found, return an
    // * empty String.
    // */
    // public String getClipboardContents() {
    // String result = "";
    // Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // //odd: the Object param of getContents is not currently used
    // Transferable contents = clipboard.getContents(null);
    // boolean hasTransferableText = (contents != null) &&
    // contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    // if (hasTransferableText) {
    // try {
    // result = (String) contents.getTransferData(DataFlavor.stringFlavor);
    // } catch (UnsupportedFlavorException ex) {
    // //highly unlikely since we are using a standard DataFlavor
    // LOG.error("UnsupportedFlavorException.");
    // ex.printStackTrace();
    // } catch (IOException ex) {
    // LOG.error("IOException.");
    // ex.printStackTrace();
    // }
    // }
    // return result;
    // }

    /**
     * Place a String on the clipboard, and make this class the owner of the
     * Clipboard's contents.
     */
    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, new ClipboardOwner() {
            @Override
            public void lostOwnership(Clipboard clipboard, Transferable contents) {

            }
        });
    }

    // private static ConfigurableApplicationContext ctx;

    /**
     * Main entry point.
     *
     * @param args args
     */
    public static void main(final String[] args) {
        // ctx = new ClassPathXmlApplicationContext(
        // "applicationContext.xml");
        // ctx.registerShutdownHook();

        MainController mainController = new MainController();
        mainController.initComponents();

        // http://blog.codecentric.de/en/2011/12/swt-and-springs-configurable-dependency-injection-for-the-ui/
    }

}
