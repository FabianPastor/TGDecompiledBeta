package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ FileRefController f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda7(FileRefController fileRefController, TLRPC$User tLRPC$User) {
        this.f$0 = fileRefController;
        this.f$1 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$onRequestComplete$30(this.f$1);
    }
}
