package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$9uOBCpumFi_Ab1S_M6zvtV6CIPg implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$9uOBCpumFi_Ab1S_M6zvtV6CIPg(MessagesStorage messagesStorage, EncryptedChat encryptedChat) {
        this.f$0 = messagesStorage;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$updateEncryptedChatLayer$104$MessagesStorage(this.f$1);
    }
}
