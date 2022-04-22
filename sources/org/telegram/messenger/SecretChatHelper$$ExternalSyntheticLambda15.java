package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$EncryptedChat;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda15(SecretChatHelper secretChatHelper, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$EncryptedChat;
    }

    public final void run() {
        this.f$0.lambda$processAcceptedSecretChat$18(this.f$1);
    }
}
