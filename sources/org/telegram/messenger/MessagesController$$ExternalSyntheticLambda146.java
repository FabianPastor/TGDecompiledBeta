package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda146 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda146(MessagesController messagesController, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$326(this.f$1);
    }
}
