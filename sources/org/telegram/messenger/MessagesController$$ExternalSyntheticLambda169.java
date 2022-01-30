package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateChatUserTyping;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda169 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_updateChatUserTyping f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda169(MessagesController messagesController, TLRPC$TL_updateChatUserTyping tLRPC$TL_updateChatUserTyping) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_updateChatUserTyping;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$300(this.f$1);
    }
}
