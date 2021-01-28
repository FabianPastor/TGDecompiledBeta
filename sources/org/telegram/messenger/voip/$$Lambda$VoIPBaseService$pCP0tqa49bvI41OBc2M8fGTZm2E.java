package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E INSTANCE = new $$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E();

    private /* synthetic */ $$Lambda$VoIPBaseService$pCP0tqa49bvI41OBc2M8fGTZm2E() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
