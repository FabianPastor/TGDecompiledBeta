package org.telegram.messenger;

import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ WearDataLayerListenerService f$0;
    public final /* synthetic */ NotificationCenter.NotificationCenterDelegate f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda6(WearDataLayerListenerService wearDataLayerListenerService, NotificationCenter.NotificationCenterDelegate notificationCenterDelegate, TLRPC$User tLRPC$User) {
        this.f$0 = wearDataLayerListenerService;
        this.f$1 = notificationCenterDelegate;
        this.f$2 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$onChannelOpened$1(this.f$1, this.f$2);
    }
}
