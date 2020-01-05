package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$JmSLMyb-373NmPAc6NmsxsfWldE implements Runnable {
    public static final /* synthetic */ -$$Lambda$Theme$JmSLMyb-373NmPAc6NmsxsfWldE INSTANCE = new -$$Lambda$Theme$JmSLMyb-373NmPAc6NmsxsfWldE();

    private /* synthetic */ -$$Lambda$Theme$JmSLMyb-373NmPAc6NmsxsfWldE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.valueOf(false));
    }
}
