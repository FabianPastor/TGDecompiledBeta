package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$vnWO6KE-tK3PoR1snVX3t9sZtgk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$vnWO6KEtK3PoR1snVX3t9sZtgk implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$vnWO6KEtK3PoR1snVX3t9sZtgk INSTANCE = new $$Lambda$VoIPService$vnWO6KEtK3PoR1snVX3t9sZtgk();

    private /* synthetic */ $$Lambda$VoIPService$vnWO6KEtK3PoR1snVX3t9sZtgk() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
