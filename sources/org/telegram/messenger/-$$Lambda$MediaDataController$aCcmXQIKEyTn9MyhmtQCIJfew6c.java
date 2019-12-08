package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$aCcmXQIKEyTn9MyhmtQCIJfew6c implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$aCcmXQIKEyTn9MyhmtQCIJfew6c(MediaDataController mediaDataController, Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$savePinnedMessage$89$MediaDataController(this.f$1);
    }
}
