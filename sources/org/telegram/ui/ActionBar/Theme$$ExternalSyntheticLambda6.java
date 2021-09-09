package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda6(boolean z) {
        this.f$0 = z;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.FALSE, Boolean.valueOf(this.f$0));
    }
}
