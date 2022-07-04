package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;
    public final /* synthetic */ TLRPC.EncryptedChat f$2;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda9(SecretChatHelper secretChatHelper, TLRPC.EncryptedChat encryptedChat, TLRPC.EncryptedChat encryptedChat2) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
        this.f$2 = encryptedChat2;
    }

    public final void run() {
        this.f$0.m2374x74cec9bb(this.f$1, this.f$2);
    }
}
