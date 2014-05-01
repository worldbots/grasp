package ru.spb.hibissscus.common;

import javax.swing.*;
import java.util.*;

/**
 * Languages Enum
 */
public enum LanguageType {
    AUTODETECTED("AutoDetected", "auto"),
    ENGLISH("English", "en"),
    DEUTSCH("Deutsch", "de"),
    RUSSIAN("Russian", "ru");

    private  String name;
    private  String sname;

    LanguageType(final String name, final String sname) {
        this.name = name;
        this.sname = sname;
    }

    private static final LinkedList<LanguageType> VALUES =
            new LinkedList<LanguageType>(Collections.unmodifiableList(Arrays.asList(values())));

    public static final LanguageType getNextLanguage(LanguageType type) {

        LanguageType result = type;
        if (VALUES.contains(type)) {
            int inputIndex = VALUES.indexOf(type);
            if (inputIndex == VALUES.size() - 1)
                result = VALUES.getFirst();
            else {
                result = VALUES.get(inputIndex + 1);
            }
        }

        return result;
    }


    public static final HashMap<LanguageType, ImageIcon> IMAGES = buildAllImage();

    private static HashMap<LanguageType, ImageIcon> buildAllImage() {
        HashMap<LanguageType, ImageIcon> images = new HashMap<LanguageType, ImageIcon>(VALUES.size());
        for (LanguageType type : values()) {
            try {
                images.put(type, IMG.icon(type.name.toLowerCase() + ".png"));
            } catch (Exception e) {
                // put default picture
            }
        }
        return images;
    }


    public static ImageIcon getIcon(final LanguageType type) {
        return IMAGES.get(type);
    }


    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static LanguageType randomLanguage() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Get shot language name
     *
     * @return shot language name
     */
    public String getShortName() {
        return sname;
    }



    public static LanguageType fromString(final String s) {
        for (LanguageType type : values()) {
            if (type.name.equals(s)) {
                return type;
            }
        }
        return null;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    /**
     * Find by short name
     *
     * @param shortName
     * @return LanguageType
     */
    public static LanguageType byShortName(final String shortName) {
        for (LanguageType type : values()) {
            if (type.sname.equals(shortName)) {
                return type;
            }
        }
        return null;
    }
}