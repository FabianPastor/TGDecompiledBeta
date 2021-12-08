package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.SharedConfig;

public final /* synthetic */ class ProxyListActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProxyListActivity f$0;
    public final /* synthetic */ SharedConfig.ProxyInfo f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ProxyListActivity$$ExternalSyntheticLambda0(ProxyListActivity proxyListActivity, SharedConfig.ProxyInfo proxyInfo, int i) {
        this.f$0 = proxyListActivity;
        this.f$1 = proxyInfo;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3829lambda$createView$1$orgtelegramuiProxyListActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
