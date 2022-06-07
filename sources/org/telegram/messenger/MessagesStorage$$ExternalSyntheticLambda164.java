package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$EncryptedChat;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda164 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda164(MessagesStorage messagesStorage, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$EncryptedChat;
    }

    public final void run() {
        this.f$0.lambda$updateEncryptedChatLayer$136(this.f$1);
    }
}
