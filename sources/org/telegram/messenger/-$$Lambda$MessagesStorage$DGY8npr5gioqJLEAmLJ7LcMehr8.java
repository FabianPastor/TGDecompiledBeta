package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$DGY8npr5gioqJLEAmLJ7LcMehr8 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$DGY8npr5gioqJLEAmLJ7LcMehr8(MessagesStorage messagesStorage, EncryptedChat encryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$updateEncryptedChatTTL$100$MessagesStorage(this.f$1);
    }
}
