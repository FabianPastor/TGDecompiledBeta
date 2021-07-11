package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU INSTANCE = new $$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU();

    private /* synthetic */ $$Lambda$VoIPService$Re89En1tN80sCvsXyVB2vhoEpYU() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
