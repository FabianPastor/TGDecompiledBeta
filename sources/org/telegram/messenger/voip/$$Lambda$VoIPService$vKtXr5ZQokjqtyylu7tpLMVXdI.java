package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpL-MVXdI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpLMVXdI implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpLMVXdI INSTANCE = new $$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpLMVXdI();

    private /* synthetic */ $$Lambda$VoIPService$vKtXr5ZQokjqtyylu7tpLMVXdI() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
