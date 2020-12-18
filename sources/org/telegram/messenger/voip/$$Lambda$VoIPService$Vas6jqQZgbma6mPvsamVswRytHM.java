package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$Vas6jqQZgbma6mPvsamVswRytHM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$Vas6jqQZgbma6mPvsamVswRytHM implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$Vas6jqQZgbma6mPvsamVswRytHM INSTANCE = new $$Lambda$VoIPService$Vas6jqQZgbma6mPvsamVswRytHM();

    private /* synthetic */ $$Lambda$VoIPService$Vas6jqQZgbma6mPvsamVswRytHM() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
