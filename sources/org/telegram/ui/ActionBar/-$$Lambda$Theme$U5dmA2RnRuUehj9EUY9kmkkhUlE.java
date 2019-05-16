package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$U5dmA2RnRuUehj9EUY9kmkkhUlE implements Runnable {
    private final /* synthetic */ boolean f$0;

    public /* synthetic */ -$$Lambda$Theme$U5dmA2RnRuUehj9EUY9kmkkhUlE(boolean z) {
        this.f$0 = z;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.valueOf(this.f$0));
    }
}
