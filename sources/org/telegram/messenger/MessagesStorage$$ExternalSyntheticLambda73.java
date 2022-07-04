package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda73(MessagesStorage messagesStorage, TLRPC.EncryptedChat encryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.m2298x58627eb0(this.f$1);
    }
}
