package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$skHxkdujbJ3-GWZNQX1vENbJMbk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$skHxkdujbJ3GWZNQX1vENbJMbk implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$skHxkdujbJ3GWZNQX1vENbJMbk INSTANCE = new $$Lambda$VoIPService$skHxkdujbJ3GWZNQX1vENbJMbk();

    private /* synthetic */ $$Lambda$VoIPService$skHxkdujbJ3GWZNQX1vENbJMbk() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
