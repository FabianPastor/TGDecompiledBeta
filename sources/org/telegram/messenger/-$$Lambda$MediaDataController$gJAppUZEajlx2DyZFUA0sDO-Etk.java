package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$gJAppUZEajlx2DyZFUA0sDO-Etk implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TL_contacts_topPeers f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$gJAppUZEajlx2DyZFUA0sDO-Etk(MediaDataController mediaDataController, TL_contacts_topPeers tL_contacts_topPeers) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_contacts_topPeers;
    }

    public final void run() {
        this.f$0.lambda$null$74$MediaDataController(this.f$1);
    }
}
