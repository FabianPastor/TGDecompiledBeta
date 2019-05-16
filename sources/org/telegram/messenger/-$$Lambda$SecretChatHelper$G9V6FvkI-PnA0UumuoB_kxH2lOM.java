package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$G9V6FvkI-PnA0UumuoB_kxH2lOM implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;
    private final /* synthetic */ EncryptedChat f$2;

    public /* synthetic */ -$$Lambda$SecretChatHelper$G9V6FvkI-PnA0UumuoB_kxH2lOM(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat, EncryptedChat encryptedChat2) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
        this.f$2 = encryptedChat2;
    }

    public final void run() {
        this.f$0.lambda$processUpdateEncryption$2$SecretChatHelper(this.f$1, this.f$2);
    }
}
