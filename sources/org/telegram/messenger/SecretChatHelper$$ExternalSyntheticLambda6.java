package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda6(SecretChatHelper secretChatHelper, TLRPC.EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.m1131lambda$applyPeerLayer$9$orgtelegrammessengerSecretChatHelper(this.f$1);
    }
}
