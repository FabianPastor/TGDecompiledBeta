package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda177 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda177(MessagesController messagesController, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$createChat$204(this.f$1);
    }
}
