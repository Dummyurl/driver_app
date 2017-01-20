package com.rideeaze.views.fragmentactivity;

import com.rideeaze.services.network.model.MenuItemData;

/**
 * Created by adventis on 2/14/16.
 */
public interface OnMenuListener {
    public void onMenuSwitchChanged(boolean isChecked, MenuItemData.MenuItemID id);
}
