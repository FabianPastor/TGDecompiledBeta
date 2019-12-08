package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$MlUmMsFmghzrY-N8hDYaGHC_dBA implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Document f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$MlUmMsFmghzrY-N8hDYaGHC_dBA(MediaDataController mediaDataController, Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.lambda$removeRecentGif$4$MediaDataController(this.f$1);
    }
}
