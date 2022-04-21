package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.EncryptedChat f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda25(SecretChatHelper secretChatHelper, int i, TLRPC.EncryptedChat encryptedChat, int i2) {
        this.f$0 = secretChatHelper;
        this.f$1 = i;
        this.f$2 = encryptedChat;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.m1091lambda$resendMessages$15$orgtelegrammessengerSecretChatHelper(this.f$1, this.f$2, this.f$3);
    }
}
