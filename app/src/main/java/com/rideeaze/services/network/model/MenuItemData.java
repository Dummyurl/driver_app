package com.rideeaze.services.network.model;

/**
 * Created by adventis on 1/16/16.
 */
public class MenuItemData {

    public MenuItemData(String title, MenuItemID id, boolean isActive) {
        setId(id);
        setTitle(title);
        setActive(isActive);
    }

    public boolean isShowSwitch() {
        return isShowSwitch;
    }

    public void setIsShowSwitch(boolean isShowSwitch) {
        this.isShowSwitch = isShowSwitch;
    }

    public boolean isShowSwitch;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MenuItemID getId() {
        return id;
    }

    public void setId(MenuItemID id) {
        this.id = id;
    }

    private String title;
    private MenuItemID id;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive;

    public enum MenuItemID {
        UPDATE_MY_VEHICLE,
        UPDATE_MY_PROFILE,
        VIEW_OR_MODIFY_TIME_OFF,
        WAKE_UP_CALL_OFF,
        PAYMENTS,
        LOGOFF
    }
}
