package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$F8aiYfUT1_9zrSVmtlo1KSXQPYk implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Document f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$F8aiYfUT1_9zrSVmtlo1KSXQPYk(MediaDataController mediaDataController, int i, Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = document;
    }

    public final void run() {
        this.f$0.lambda$addRecentSticker$1$MediaDataController(this.f$1, this.f$2);
    }
}
