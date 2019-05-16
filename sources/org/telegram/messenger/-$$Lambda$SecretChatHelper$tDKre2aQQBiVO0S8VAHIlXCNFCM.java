package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$tDKre2aQQBiVO0S8VAHIlXCNFCM implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$tDKre2aQQBiVO0S8VAHIlXCNFCM(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$processAcceptedSecretChat$17$SecretChatHelper(this.f$1);
    }
}
