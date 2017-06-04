package com.dcmmoguls.offthejailadmin;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ChannelItem {

    public String key;
    public Object message;
    public String name;
    public String city;
    public String phone;
    public String email;

    public ChannelItem() {
    }

    ChannelItem(Object message, String name, String city, String phone, String email) {
        this.message = message;
        this.name = name;
        this.city = city;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}
