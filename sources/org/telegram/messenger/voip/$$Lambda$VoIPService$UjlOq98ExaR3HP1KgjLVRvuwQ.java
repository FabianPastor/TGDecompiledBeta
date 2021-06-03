package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$UjlOq9-8-ExaR3HP1KgjLVRvuwQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$UjlOq98ExaR3HP1KgjLVRvuwQ implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$UjlOq98ExaR3HP1KgjLVRvuwQ INSTANCE = new $$Lambda$VoIPService$UjlOq98ExaR3HP1KgjLVRvuwQ();

    private /* synthetic */ $$Lambda$VoIPService$UjlOq98ExaR3HP1KgjLVRvuwQ() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
