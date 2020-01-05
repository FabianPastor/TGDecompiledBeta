package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$h9Jzpt5s3PxTe8JZLRUPYrvDrIE implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$h9Jzpt5s3PxTe8JZLRUPYrvDrIE(MediaDataController mediaDataController, Message message, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$null$108$MediaDataController(this.f$1, this.f$2);
    }
}
