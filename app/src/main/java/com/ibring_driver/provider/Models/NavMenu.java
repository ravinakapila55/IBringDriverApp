package com.ibring_driver.provider.Models;

import com.ibring_driver.provider.R;

public enum NavMenu {
    HOME(R.string.menu_home),
    SETTINGS(R.string.menu_settings),
    SHARE(R.string.menu_share),
    LOGOUT(R.string.menu_logout),
    HISTORY(R.string.menu_history),
    SUMMARY(R.string.summary),
    HELP(R.string.help),
    EARNINGS(R.string.earnings);

    private int stringId;

    NavMenu(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

}
