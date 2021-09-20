package org.telegram.ui;

import org.telegram.messenger.SharedConfig;

public final /* synthetic */ class ProxyListActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SharedConfig.ProxyInfo f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ProxyListActivity$$ExternalSyntheticLambda1(SharedConfig.ProxyInfo proxyInfo, long j) {
        this.f$0 = proxyInfo;
        this.f$1 = j;
    }

    public final void run() {
        ProxyListActivity.lambda$checkProxyList$3(this.f$0, this.f$1);
    }
}
