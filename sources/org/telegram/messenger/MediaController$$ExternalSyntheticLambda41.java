package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ TLRPC.Document f$1;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda41(AccountInstance accountInstance, TLRPC.Document document) {
        this.f$0 = accountInstance;
        this.f$1 = document;
    }

    public final void run() {
        this.f$0.getFileLoader().loadFile(this.f$1, (Object) null, 1, 1);
    }
}
