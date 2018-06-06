package saufverwaltung.util;

import java.util.ResourceBundle;

public class Localizer {
    private final ResourceBundle msg;

    public Localizer(ResourceBundle msg) {
        this.msg = msg;
    }

    public String getString(String key) {
        return msg.getString(key);
    }
}
