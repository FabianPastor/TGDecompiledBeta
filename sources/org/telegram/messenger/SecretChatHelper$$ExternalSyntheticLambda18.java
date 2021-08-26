package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$EncryptedChat;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;
    public final /* synthetic */ TLRPC$EncryptedChat f$2;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda18(SecretChatHelper secretChatHelper, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$EncryptedChat tLRPC$EncryptedChat2) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$EncryptedChat;
        this.f$2 = tLRPC$EncryptedChat2;
    }

    public final void run() {
        this.f$0.lambda$processUpdateEncryption$2(this.f$1, this.f$2);
    }
}
