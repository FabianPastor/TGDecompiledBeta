package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda30 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Document f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda30(MediaDataController mediaDataController, int i, TLRPC$Document tLRPC$Document) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$addRecentSticker$22(this.f$1, this.f$2);
    }
}
