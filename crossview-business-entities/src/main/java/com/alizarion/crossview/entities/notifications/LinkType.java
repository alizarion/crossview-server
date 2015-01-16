package com.alizarion.crossview.entities.notifications;

/**
 * @author selim@openlinux.fr
 */
public enum LinkType  {

    A("ALLEGE"),
    C("CROSS"),
    R("REFUTE");

    private String key;

    LinkType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
