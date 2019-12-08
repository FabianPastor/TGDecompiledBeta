package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$Obnvx1u5j3b48GMDjUB8BgBhSxI implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Message f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$Obnvx1u5j3b48GMDjUB8BgBhSxI(MediaDataController mediaDataController, long j, Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.lambda$saveDraftReplyMessage$105$MediaDataController(this.f$1, this.f$2);
    }
}
