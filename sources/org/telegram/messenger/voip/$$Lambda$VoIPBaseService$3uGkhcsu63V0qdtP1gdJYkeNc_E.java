package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPBaseService$3uGkhcsu63V0qdtP1gdJYkeNc_E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPBaseService$3uGkhcsu63V0qdtP1gdJYkeNc_E implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPBaseService$3uGkhcsu63V0qdtP1gdJYkeNc_E INSTANCE = new $$Lambda$VoIPBaseService$3uGkhcsu63V0qdtP1gdJYkeNc_E();

    private /* synthetic */ $$Lambda$VoIPBaseService$3uGkhcsu63V0qdtP1gdJYkeNc_E() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
