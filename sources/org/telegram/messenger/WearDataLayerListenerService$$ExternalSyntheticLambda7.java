package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CyclicBarrier;
import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda7 implements NotificationCenter.NotificationCenterDelegate {
    public final /* synthetic */ File f$0;
    public final /* synthetic */ CyclicBarrier f$1;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda7(File file, CyclicBarrier cyclicBarrier) {
        this.f$0 = file;
        this.f$1 = cyclicBarrier;
    }

    public final void didReceivedNotification(int i, int i2, Object[] objArr) {
        WearDataLayerListenerService.lambda$onChannelOpened$0(this.f$0, this.f$1, i, i2, objArr);
    }
}
