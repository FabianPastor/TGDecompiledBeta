package org.telegram.ui;

import org.telegram.messenger.SharedConfig.ProxyInfo;

final /* synthetic */ class ProxyListActivity$$Lambda$3 implements Runnable {
    private final ProxyInfo arg$1;
    private final long arg$2;

    ProxyListActivity$$Lambda$3(ProxyInfo proxyInfo, long j) {
        this.arg$1 = proxyInfo;
        this.arg$2 = j;
    }

    public void run() {
        ProxyListActivity.lambda$null$3$ProxyListActivity(this.arg$1, this.arg$2);
    }
}
