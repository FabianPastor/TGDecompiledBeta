package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$jOWYAPnKORUAYTgI9RfoSTvJVpA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$jOWYAPnKORUAYTgI9RfoSTvJVpA implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$jOWYAPnKORUAYTgI9RfoSTvJVpA INSTANCE = new $$Lambda$VoIPService$jOWYAPnKORUAYTgI9RfoSTvJVpA();

    private /* synthetic */ $$Lambda$VoIPService$jOWYAPnKORUAYTgI9RfoSTvJVpA() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
