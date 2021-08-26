package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.RequestTimeDelegate;

public final /* synthetic */ class ProxyListActivity$$ExternalSyntheticLambda2 implements RequestTimeDelegate {
    public final /* synthetic */ SharedConfig.ProxyInfo f$0;

    public /* synthetic */ ProxyListActivity$$ExternalSyntheticLambda2(SharedConfig.ProxyInfo proxyInfo) {
        this.f$0 = proxyInfo;
    }

    public final void run(long j) {
        AndroidUtilities.runOnUIThread(new ProxyListActivity$$ExternalSyntheticLambda1(this.f$0, j));
    }
}
