package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda77(MediaDataController mediaDataController, TLRPC$Document tLRPC$Document) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$addRecentGif$7(this.f$1);
    }
}
