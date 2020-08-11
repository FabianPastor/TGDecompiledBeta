package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPBaseService$P0JaABs0QNj6qdlGVIvCURoOJAE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPBaseService$P0JaABs0QNj6qdlGVIvCURoOJAE implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPBaseService$P0JaABs0QNj6qdlGVIvCURoOJAE INSTANCE = new $$Lambda$VoIPBaseService$P0JaABs0QNj6qdlGVIvCURoOJAE();

    private /* synthetic */ $$Lambda$VoIPBaseService$P0JaABs0QNj6qdlGVIvCURoOJAE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
