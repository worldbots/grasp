package ru.spb.hibissscus.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Settings
 */
@Getter
@Setter
@AllArgsConstructor
public class Settings {

    /**
     * From Language
     */
    private LanguageType fromLanguage;
    /**
     * Target language
     */
    private LanguageType toLanguage;
    private boolean proxy;
    private boolean balloon;

    private boolean voice;
    /**
     * Make a voice to translation in "to" language
     * or in "from" language
     */
    private boolean voiceTo;
    private boolean translate;
    private boolean trayTransaltion;

    /**
     * proxy = true;
     * balloon = true;
     * voice = true;
     * voiceTo = false;
     * translate = true;
     * trayTransaltion = true;
     *
     * @return
     */
    public static Settings defaultSettings() {
        return new Settings(LanguageType.AUTODETECTED, LanguageType.RUSSIAN, false, false, true, false, true, true);
    }

}
