package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$BIMksq3odPWDbPzozQvsGbTAFVQ implements Runnable {
    private final /* synthetic */ FileRefController f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$FileRefController$BIMksq3odPWDbPzozQvsGbTAFVQ(FileRefController fileRefController, User user) {
        this.f$0 = fileRefController;
        this.f$1 = user;
    }

    public final void run() {
        this.f$0.lambda$onRequestComplete$26$FileRefController(this.f$1);
    }
}
