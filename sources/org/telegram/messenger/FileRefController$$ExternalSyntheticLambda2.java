package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ FileRefController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda2(FileRefController fileRefController, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = fileRefController;
        this.f$1 = tLRPC$Chat;
    }

    public final void run() {
        this.f$0.lambda$onRequestComplete$31(this.f$1);
    }
}
