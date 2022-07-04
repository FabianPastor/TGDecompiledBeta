package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda129 implements Runnable {
    public final /* synthetic */ MessagesStorage.LongCallback f$0;
    public final /* synthetic */ TLRPC.Updates f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda129(MessagesStorage.LongCallback longCallback, TLRPC.Updates updates) {
        this.f$0 = longCallback;
        this.f$1 = updates;
    }

    public final void run() {
        MessagesController.lambda$convertToMegaGroup$214(this.f$0, this.f$1);
    }
}
