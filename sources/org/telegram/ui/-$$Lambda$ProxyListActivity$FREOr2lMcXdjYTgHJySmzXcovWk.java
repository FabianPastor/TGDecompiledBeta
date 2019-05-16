package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.tgnet.RequestTimeDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxyListActivity$FREOr2lMcXdjYTgHJySmzXcovWk implements RequestTimeDelegate {
    private final /* synthetic */ ProxyInfo f$0;

    public /* synthetic */ -$$Lambda$ProxyListActivity$FREOr2lMcXdjYTgHJySmzXcovWk(ProxyInfo proxyInfo) {
        this.f$0 = proxyInfo;
    }

    public final void run(long j) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ProxyListActivity$GXujEQ9q4Ax5-ntFNj2k4M90nXg(this.f$0, j));
    }
}
