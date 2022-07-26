package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda81 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$Document f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda81(MediaDataController mediaDataController, TLObject tLObject, TLRPC$Document tLRPC$Document) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$saveToRingtones$173(this.f$1, this.f$2);
    }
}
