package org.telegram.messenger;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ WearDataLayerListenerService f$0;
    public final /* synthetic */ NotificationCenter.NotificationCenterDelegate f$1;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda3(WearDataLayerListenerService wearDataLayerListenerService, NotificationCenter.NotificationCenterDelegate notificationCenterDelegate) {
        this.f$0 = wearDataLayerListenerService;
        this.f$1 = notificationCenterDelegate;
    }

    public final void run() {
        this.f$0.lambda$onChannelOpened$5(this.f$1);
    }
}
