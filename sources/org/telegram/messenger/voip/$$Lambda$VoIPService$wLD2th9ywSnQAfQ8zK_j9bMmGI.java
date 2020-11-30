package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_-j9bMmGI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_j9bMmGI implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_j9bMmGI INSTANCE = new $$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_j9bMmGI();

    private /* synthetic */ $$Lambda$VoIPService$wLD2th9ywSnQAfQ8zK_j9bMmGI() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
