package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda75 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda75(MessagesStorage messagesStorage, TLRPC.EncryptedChat encryptedChat, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m2297xb8a5257e(this.f$1, this.f$2);
    }
}
