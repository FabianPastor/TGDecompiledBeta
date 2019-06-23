package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$w3yTY-QxW_L1Z8Vv3ZI2F9q0Qtc implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Document f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$w3yTY-QxW_L1Z8Vv3ZI2F9q0Qtc(MediaDataController mediaDataController, Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.lambda$removeRecentGif$3$MediaDataController(this.f$1);
    }
}
