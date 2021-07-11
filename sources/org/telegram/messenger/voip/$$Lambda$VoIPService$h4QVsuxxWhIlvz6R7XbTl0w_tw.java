package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$h4QVsuxxWhIlv-z6R7XbTl0w_tw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$h4QVsuxxWhIlvz6R7XbTl0w_tw implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$h4QVsuxxWhIlvz6R7XbTl0w_tw INSTANCE = new $$Lambda$VoIPService$h4QVsuxxWhIlvz6R7XbTl0w_tw();

    private /* synthetic */ $$Lambda$VoIPService$h4QVsuxxWhIlvz6R7XbTl0w_tw() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
