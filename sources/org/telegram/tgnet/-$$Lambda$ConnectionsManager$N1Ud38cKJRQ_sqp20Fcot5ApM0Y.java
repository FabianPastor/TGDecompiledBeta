package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$N1Ud38cKJRQ_sqp20Fcot5ApM0Y implements Runnable {
    private final /* synthetic */ RequestDelegate f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_error f$2;

    public /* synthetic */ -$$Lambda$ConnectionsManager$N1Ud38cKJRQ_sqp20Fcot5ApM0Y(RequestDelegate requestDelegate, TLObject tLObject, TL_error tL_error) {
        this.f$0 = requestDelegate;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
    }

    public final void run() {
        ConnectionsManager.lambda$null$0(this.f$0, this.f$1, this.f$2);
    }
}
