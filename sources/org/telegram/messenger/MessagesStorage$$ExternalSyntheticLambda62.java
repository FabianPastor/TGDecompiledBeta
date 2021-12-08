package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda62 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda62(MessagesStorage messagesStorage, TLRPC.EncryptedChat encryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.m1072xd89b7256(this.f$1);
    }
}
