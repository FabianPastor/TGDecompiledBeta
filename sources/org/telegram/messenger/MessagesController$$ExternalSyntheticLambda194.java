package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda194 implements Runnable {
    public final /* synthetic */ MessagesStorage.LongCallback f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda194(MessagesStorage.LongCallback longCallback, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = longCallback;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        MessagesController.lambda$convertToMegaGroup$203(this.f$0, this.f$1);
    }
}