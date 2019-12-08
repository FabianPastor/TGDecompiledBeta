package org.telegram.messenger;

import java.util.concurrent.CyclicBarrier;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$6oPgRZPy8Zd89HYJVV7iQJ7-ETw implements NotificationCenterDelegate {
    private final /* synthetic */ String[] f$0;
    private final /* synthetic */ CyclicBarrier f$1;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$6oPgRZPy8Zd89HYJVV7iQJ7-ETw(String[] strArr, CyclicBarrier cyclicBarrier) {
        this.f$0 = strArr;
        this.f$1 = cyclicBarrier;
    }

    public final void didReceivedNotification(int i, int i2, Object[] objArr) {
        WearDataLayerListenerService.lambda$onChannelOpened$3(this.f$0, this.f$1, i, i2, objArr);
    }
}
