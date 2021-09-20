package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda7(AccountInstance accountInstance, TLRPC$Document tLRPC$Document) {
        this.f$0 = accountInstance;
        this.f$1 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.getFileLoader().loadFile(this.f$1, (Object) null, 1, 1);
    }
}
