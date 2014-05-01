package ru.spb.hibissscus.client.tray;

import lombok.Getter;
import ru.spb.hibissscus.common.LanguageType;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * LanguageItem for awt menu
 */
class LanguageItem extends MenuItem {

    @Getter  private LanguageType languageType;
    private ActionListener listener;

    LanguageItem(LanguageType languageType, ActionListener listener) {
        super();
        this.languageType = languageType;
        this.listener = listener;

        this.setLabel(this.languageType.toString());
        this.addActionListener(this.listener);
    }

}
