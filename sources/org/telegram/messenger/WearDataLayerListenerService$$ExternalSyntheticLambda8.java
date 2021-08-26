package org.telegram.messenger;

import java.util.concurrent.CyclicBarrier;
import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda8 implements NotificationCenter.NotificationCenterDelegate {
    public final /* synthetic */ String[] f$0;
    public final /* synthetic */ CyclicBarrier f$1;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda8(String[] strArr, CyclicBarrier cyclicBarrier) {
        this.f$0 = strArr;
        this.f$1 = cyclicBarrier;
    }

    public final void didReceivedNotification(int i, int i2, Object[] objArr) {
        WearDataLayerListenerService.lambda$onChannelOpened$3(this.f$0, this.f$1, i, i2, objArr);
    }
}
