package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.tgnet.RequestTimeDelegate;

final /* synthetic */ class ProxyListActivity$$Lambda$2 implements RequestTimeDelegate {
    private final ProxyInfo arg$1;

    ProxyListActivity$$Lambda$2(ProxyInfo proxyInfo) {
        this.arg$1 = proxyInfo;
    }

    public void run(long j) {
        AndroidUtilities.runOnUIThread(new ProxyListActivity$$Lambda$3(this.arg$1, j));
    }
}
