package org.telegram.messenger;

import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$CP3JJJCVrqGAns9jccdML_vquXc implements Runnable {
    private final /* synthetic */ WearDataLayerListenerService f$0;
    private final /* synthetic */ NotificationCenterDelegate f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$CP3JJJCVrqGAns9jccdML_vquXc(WearDataLayerListenerService wearDataLayerListenerService, NotificationCenterDelegate notificationCenterDelegate, User user) {
        this.f$0 = wearDataLayerListenerService;
        this.f$1 = notificationCenterDelegate;
        this.f$2 = user;
    }

    public final void run() {
        this.f$0.lambda$onChannelOpened$1$WearDataLayerListenerService(this.f$1, this.f$2);
    }
}
