package org.telegram.messenger;

import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$66bw8jSTVlEhrlsDPfucvOtg7dU implements Runnable {
    private final /* synthetic */ WearDataLayerListenerService f$0;
    private final /* synthetic */ NotificationCenterDelegate f$1;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$66bw8jSTVlEhrlsDPfucvOtg7dU(WearDataLayerListenerService wearDataLayerListenerService, NotificationCenterDelegate notificationCenterDelegate) {
        this.f$0 = wearDataLayerListenerService;
        this.f$1 = notificationCenterDelegate;
    }

    public final void run() {
        this.f$0.lambda$onChannelOpened$4$WearDataLayerListenerService(this.f$1);
    }
}
