package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$zNpQNo9DufTgGtyCtSE0gqWF9mA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$zNpQNo9DufTgGtyCtSE0gqWF9mA implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$zNpQNo9DufTgGtyCtSE0gqWF9mA INSTANCE = new $$Lambda$VoIPService$zNpQNo9DufTgGtyCtSE0gqWF9mA();

    private /* synthetic */ $$Lambda$VoIPService$zNpQNo9DufTgGtyCtSE0gqWF9mA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
