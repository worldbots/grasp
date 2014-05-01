package ru.spb.hibissscus.client.tray;

import com.melloware.jintellitype.JIntellitype;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spb.hibissscus.common.IMG;
import ru.spb.hibissscus.common.LanguageType;
import ru.spb.hibissscus.common.Settings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Main class for system tray
 * TODO decouple
 */
@Getter
@Setter
public class TrayClient {

    private static final Logger LOG = LoggerFactory.getLogger(TrayClient.class);


    /**
     * Title in tray
     */
    private String title;

    /**
     * System tray for text translation
     */
    private TrayIcon trayIcon;

    /**
     * Input text, which we try to show in tray
     */
    private String text;


    // TODO make like as additional class
    /**
     * Main popup
     */
    private PopupMenu mainPopup = new PopupMenu();

    private Settings settings = Settings.defaultSettings();


    /**
     * Some of checkboxes
     */
    private CheckboxMenuItem proxy = new CheckboxMenuItem("Use Proxy");
    private CheckboxMenuItem balloon = new CheckboxMenuItem("Show Balloon on the screen");
    private CheckboxMenuItem voice = new CheckboxMenuItem("Voice translation");
    private CheckboxMenuItem voiceTo = new CheckboxMenuItem("Voice translation or text");
    private CheckboxMenuItem tray = new CheckboxMenuItem("Show translation into Tray");


    public TrayClient() {
        initComponents();
    }


    /**
     * Show text in tray
     */
    public void showText(String title, String text) {
        this.text = text;
        this.title = title;

        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }

    /**
     * Show text in tray
     */
    public void showText(String title, java.util.List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }
        this.text = sb.toString();
        this.title = title;
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }


    /**
     * Switch languages in tray
     */
    public void switchLanguages() {
        // don't switch languages if it's auto
        if (getSettings().getFromLanguage() != LanguageType.AUTODETECTED
                && getSettings().getToLanguage() != LanguageType.AUTODETECTED) {

            LanguageType tmp = settings.getFromLanguage();
            LanguageType fromLanguage = settings.getToLanguage();
            LanguageType toLanguage = tmp;

            // TODO add change for awt language items
            changeMenuLanguage("From", fromLanguage);
            changeMenuLanguage("To", toLanguage);

            changeLanguage();
        }
    }

    /**
     * Spin languages in tray
     */
    public void spinLanguages(boolean left) {

        LanguageType fromLanguage = getSettings().getFromLanguage();
        LanguageType toLanguage = getSettings().getToLanguage();

        if (left) {
            fromLanguage = LanguageType.getNextLanguage(fromLanguage);
            // skip language
            while (fromLanguage == toLanguage)
                fromLanguage = LanguageType.getNextLanguage(fromLanguage);
        } else {
            toLanguage = LanguageType.getNextLanguage(toLanguage);

            while (toLanguage == fromLanguage || toLanguage == LanguageType.AUTODETECTED) {
                toLanguage = LanguageType.getNextLanguage(toLanguage);
            }

        }

        changeMenuLanguage("From", fromLanguage);
        changeMenuLanguage("To", toLanguage);

        changeLanguage();
    }


    /**
     * Hard Code. Information about app  TODO fixme
     */
    private static final String aboutText = "Quickly! \nJava tray client based on Google Translate! " +
            "Directly, without any Google API! You can get translation really quickly without no pain, just one click! " +
            "This software tool supports voice-translation of the text. \n\nAuthor: Stepanov Sergey ^_^";    /**

     * Hard Code. Information about app  TODO fixme
     */
    private static final String hotKey = "" +
                    "Move mouse on the word and click <Win + C> \n" +
                    "or you can copy the word in standart way use <Ctrl+C> and after that click <Ctrl+Shift+C>. \n" +
                    "\n" +
                    "<Ctrl+Shift+Z> or <Ctrl+Shift+X> change language\n" +
                    "<Ctrl+Shift+S> languages swap\n" +
                    "<Ctrl+Shift+V> make voice\n";


    /**
     * Initialization main component
     */
    private void initComponents() {
        final SystemTray systemTray = SystemTray.getSystemTray();

        final Image image = IMG.combineIcons(LanguageType.getIcon(getSettings().getFromLanguage()),
                LanguageType.getIcon(getSettings().getToLanguage())
        );

        trayIcon = new TrayIcon(image, "Quickly :)", mainPopup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setImage(image);
        changeLanguage();


        /**
         * Show last text
         */
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (text != null)
                    trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
            }
        });

        //Language subpanel
        PopupMenu fromPopup = new PopupMenu("From " + getSettings().getFromLanguage());
        PopupMenu toPopup = new PopupMenu("To " + getSettings().getToLanguage());

        // Exit
        final ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LOG.info("Exiting Application");
                // don't forget to clean up any resources before close
                JIntellitype.getInstance().cleanUp();
                System.exit(0);
            }
        };
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(exitListener);


        // awt hell
        ActionListener fromListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LanguageItem item = (LanguageItem) e.getSource();
                PopupMenu parent = (PopupMenu) item.getParent();
                parent.setLabel("From " + item.getLabel());
                getSettings().setFromLanguage(item.getLanguageType());

                changeLanguage();

            }
        };
        ActionListener toListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LanguageItem item = (LanguageItem) e.getSource();
                PopupMenu parent = (PopupMenu) item.getParent();
                parent.setLabel("To " + item.getLabel());
                getSettings().setToLanguage(item.getLanguageType());

                changeLanguage();
            }
        };


        fromPopup.add(new LanguageItem(LanguageType.AUTODETECTED, fromListener));
        fromPopup.addSeparator();
        fromPopup.add(new LanguageItem(LanguageType.ENGLISH, fromListener));
        fromPopup.add(new LanguageItem(LanguageType.DEUTSCH, fromListener));
        fromPopup.add(new LanguageItem(LanguageType.RUSSIAN, fromListener));

        toPopup.add(new LanguageItem(LanguageType.ENGLISH, toListener));
        toPopup.add(new LanguageItem(LanguageType.DEUTSCH, toListener));
        toPopup.add(new LanguageItem(LanguageType.RUSSIAN, toListener));


        //TODO FIXME: add help inforamtion
        MenuItem hotKeyItem = new MenuItem("HotKeys");
        hotKeyItem.addActionListener(
                new ShowMessageListener(trayIcon, null, hotKey, TrayIcon.MessageType.NONE)
        );

        //TODO FIXME: add help inforamtion
        MenuItem helpItem = new MenuItem("About");
        helpItem.addActionListener(
                new ShowMessageListener(trayIcon, null, aboutText, TrayIcon.MessageType.NONE)
        );


        //TODO FIXME: add proxy settings
        PopupMenu settingsItem = new PopupMenu("Settings");


        //Balloon
        balloon.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getSettings().setBalloon(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        settingsItem.add(balloon);
        changeBalloon(getSettings().isBalloon());

        //Voice
        voice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getSettings().setVoice(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        settingsItem.add(voice);
        changeVoice(getSettings().isVoice());

        //Tray
        tray.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getSettings().setTrayTransaltion(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        settingsItem.add(tray);
        changeTrayTranslation(getSettings().isTrayTransaltion());

        //Proxy
        proxy.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getSettings().setProxy(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        settingsItem.add(proxy);
        changeProxy(getSettings().isProxy());

        //voiceTo
        voiceTo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                getSettings().setVoiceTo(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        settingsItem.add(voiceTo);
        changeVoiceTo(getSettings().isVoiceTo());


        mainPopup.add(hotKeyItem);
        mainPopup.add(settingsItem);
        mainPopup.addSeparator();
        mainPopup.add(fromPopup);
        mainPopup.add(fromPopup);
        mainPopup.add(toPopup);
        mainPopup.addSeparator();
        mainPopup.add(helpItem);
        mainPopup.add(exitItem);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            LOG.error("TrayIcon could not be added.");
        }
    }

    /**
     * Change Proxy CheckboxMenuItem
     *
     * @param use boolean flag how to make change
     */
    private void changeProxy(boolean use) {
        proxy.setState(use);
        getSettings().setProxy(use);
    }

    /**
     * Change Balloon CheckboxMenuItem
     *
     * @param use boolean flag how to make change
     */
    private void changeBalloon(boolean use) {
        balloon.setState(use);
        getSettings().setBalloon(use);
    }

    /**
     * Change Voice CheckboxMenuItem
     *
     * @param use boolean flag how to make change
     */
    private void changeVoice(boolean use) {
        voice.setState(use);
        getSettings().setVoice(use);
    }

    /**
     * Change Tray translation CheckboxMenuItem
     *
     * @param use boolean flag how to make change
     */
    private void changeTrayTranslation(boolean use) {
        tray.setState(use);
        getSettings().setTrayTransaltion(use);
    }


    /**
     * Change Voice "to" language or "from"
     *
     * @param use boolean flag how to make change
     */
    private void changeVoiceTo(boolean use) {
        voiceTo.setState(use);
        getSettings().setVoiceTo(use);
    }

    /**
     * Change tray icon after language changing
     */
    private void changeLanguage() {

        LanguageType fromLanguage = getSettings().getFromLanguage();
        LanguageType toLanguage = getSettings().getToLanguage();

        trayIcon.setToolTip(fromLanguage + " -> " + toLanguage);

        // if from Language it's Auto language we must show only to language
        if (fromLanguage == LanguageType.AUTODETECTED)
            trayIcon.setImage(
                    IMG.iconToImage(LanguageType.getIcon(toLanguage)));
        else
            trayIcon.setImage(
                    IMG.combineIcons(LanguageType.getIcon(fromLanguage),
                            LanguageType.getIcon(toLanguage)
                    ));

    }

    /**
     * Change awt item in tray menu
     */
    private void changeMenuLanguage(String namePrefix, LanguageType language) {

        int itemCount = mainPopup.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            MenuItem item = mainPopup.getItem(i);
            String name = item.getLabel();
            if (name.startsWith(namePrefix)) {
                item.setLabel(namePrefix + " " + language.name());
            }
        }

    }

    public static void main(String[] args) {
        TrayClient trayClient = new TrayClient();
        trayClient.showText("test", "test");
    }


}
