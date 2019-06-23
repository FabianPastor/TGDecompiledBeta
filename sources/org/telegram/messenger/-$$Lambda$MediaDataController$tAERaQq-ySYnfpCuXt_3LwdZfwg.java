package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$tAERaQq-ySYnfpCuXt_3LwdZfwg implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Document f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$tAERaQq-ySYnfpCuXt_3LwdZfwg(MediaDataController mediaDataController, Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.lambda$addRecentGif$4$MediaDataController(this.f$1);
    }
}
