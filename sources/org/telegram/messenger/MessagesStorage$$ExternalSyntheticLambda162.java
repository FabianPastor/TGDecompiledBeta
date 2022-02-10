package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$EncryptedChat;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda162 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda162(MessagesStorage messagesStorage, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$EncryptedChat;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$updateEncryptedChatSeq$130(this.f$1, this.f$2);
    }
}
