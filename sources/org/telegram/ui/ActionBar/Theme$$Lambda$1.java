package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

final /* synthetic */ class Theme$$Lambda$1 implements Runnable {
    private final boolean arg$1;

    Theme$$Lambda$1(boolean z) {
        this.arg$1 = z;
    }

    public void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.valueOf(this.arg$1));
    }
}
