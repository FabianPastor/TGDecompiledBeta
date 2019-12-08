package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ApMTf7BSKef7cNrXIdksWSeKDDA implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Document f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$ApMTf7BSKef7cNrXIdksWSeKDDA(MediaDataController mediaDataController, int i, Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = document;
    }

    public final void run() {
        this.f$0.lambda$addRecentSticker$2$MediaDataController(this.f$1, this.f$2);
    }
}
