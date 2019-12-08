package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$vL56QOhGq-BG-IqB3wL5QCBk7es implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$vL56QOhGq-BG-IqB3wL5QCBk7es(MessagesStorage messagesStorage, EncryptedChat encryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$updateEncryptedChat$106$MessagesStorage(this.f$1);
    }
}
