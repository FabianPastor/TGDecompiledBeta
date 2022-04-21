package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda8(boolean z) {
        this.f$0 = z;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false, Boolean.valueOf(this.f$0));
    }
}
