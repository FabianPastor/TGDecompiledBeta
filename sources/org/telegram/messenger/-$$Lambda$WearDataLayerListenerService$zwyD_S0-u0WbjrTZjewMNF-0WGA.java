package org.telegram.messenger;

import java.io.File;
import java.util.concurrent.CyclicBarrier;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$zwyD_S0-u0WbjrTZjewMNF-0WGA implements NotificationCenterDelegate {
    private final /* synthetic */ File f$0;
    private final /* synthetic */ CyclicBarrier f$1;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$zwyD_S0-u0WbjrTZjewMNF-0WGA(File file, CyclicBarrier cyclicBarrier) {
        this.f$0 = file;
        this.f$1 = cyclicBarrier;
    }

    public final void didReceivedNotification(int i, int i2, Object[] objArr) {
        WearDataLayerListenerService.lambda$onChannelOpened$0(this.f$0, this.f$1, i, i2, objArr);
    }
}
