package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$GU2GL7mWhGUnYQh5I_TbKMMIkw4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$GU2GL7mWhGUnYQh5I_TbKMMIkw4 implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$GU2GL7mWhGUnYQh5I_TbKMMIkw4 INSTANCE = new $$Lambda$VoIPService$GU2GL7mWhGUnYQh5I_TbKMMIkw4();

    private /* synthetic */ $$Lambda$VoIPService$GU2GL7mWhGUnYQh5I_TbKMMIkw4() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
