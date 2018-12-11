package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SharedConfig.ProxyInfo;

/* renamed from: org.telegram.ui.ProxyListActivity$$Lambda$4 */
final /* synthetic */ class ProxyListActivity$$Lambda$4 implements OnClickListener {
    private final ProxyListActivity arg$1;
    private final ProxyInfo arg$2;

    ProxyListActivity$$Lambda$4(ProxyListActivity proxyListActivity, ProxyInfo proxyInfo) {
        this.arg$1 = proxyListActivity;
        this.arg$2 = proxyInfo;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$ProxyListActivity(this.arg$2, dialogInterface, i);
    }
}
