package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateUserTyping;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda183 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_updateUserTyping f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda183(MessagesController messagesController, TLRPC$TL_updateUserTyping tLRPC$TL_updateUserTyping) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_updateUserTyping;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$311(this.f$1);
    }
}
