package org.telegram.tgnet;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_config;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$wiVMBnjTO-Ju65Uh4sMTb_loS8A implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ TL_config f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$wiVMBnjTO-Ju65Uh4sMTb_loS8A(int i, TL_config tL_config) {
        this.f$0 = i;
        this.f$1 = tL_config;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).updateConfig(this.f$1);
    }
}
