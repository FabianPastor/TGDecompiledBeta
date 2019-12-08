package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SharedConfig.ProxyInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxyListActivity$OajISs3QuB64bwKXTSHCR_ffCLASSNAME implements OnClickListener {
    private final /* synthetic */ ProxyListActivity f$0;
    private final /* synthetic */ ProxyInfo f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$ProxyListActivity$OajISs3QuB64bwKXTSHCR_ffCLASSNAME(ProxyListActivity proxyListActivity, ProxyInfo proxyInfo, int i) {
        this.f$0 = proxyListActivity;
        this.f$1 = proxyInfo;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$1$ProxyListActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
