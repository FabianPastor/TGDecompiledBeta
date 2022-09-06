package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda188 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda188(MessagesController messagesController, TLRPC$User tLRPC$User) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$loadFullUser$50(this.f$1);
    }
}
