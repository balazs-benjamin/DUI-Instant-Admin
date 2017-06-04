package com.dcmmoguls.offthejailadmin;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class UserItem {

    public String key;
    public String name;
    public String city;
    public String phone;
    public String email;
    public String OneSignalId;
    private boolean isSelected = false;

    public UserItem() {
    }

    UserItem(String name, String city, String phone, String email, String OneSignalId) {
        this.name = name;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.OneSignalId = OneSignalId;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return name;
    }
}
