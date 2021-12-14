package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateChannel;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda161 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_updateChannel f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda161(MessagesController messagesController, TLRPC$TL_updateChannel tLRPC$TL_updateChannel) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_updateChannel;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$307(this.f$1);
    }
}
