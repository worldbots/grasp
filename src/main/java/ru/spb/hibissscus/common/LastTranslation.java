package ru.spb.hibissscus.common;

/**
 * Last translation
 */
public class LastTranslation {

    private String fromWord;
    private String toWord;

    private LanguageType fromLanguage;
    private LanguageType toLanguage;

    public LastTranslation() {
    }

    public LastTranslation(String fromWord, LanguageType fromLanguage, LanguageType toLanguage, String toWord) {
        this.fromWord = fromWord;
        this.toWord = toWord;
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }

    public String getFromWord() {
        return fromWord;
    }

    public void setFromWord(String fromWord) {
        this.fromWord = fromWord;
    }

    public String getToWord() {
        return toWord;
    }

    public void setToWord(String toWord) {
        this.toWord = toWord;
    }

    public LanguageType getFromLanguage() {
        return fromLanguage;
    }

    public void setFromLanguage(LanguageType fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public LanguageType getToLanguage() {
        return toLanguage;
    }

    public void setToLanguage(LanguageType toLanguage) {
        this.toLanguage = toLanguage;
    }
}
