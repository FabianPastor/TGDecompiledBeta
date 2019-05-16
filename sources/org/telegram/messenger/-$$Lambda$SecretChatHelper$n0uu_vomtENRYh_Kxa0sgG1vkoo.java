package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$n0uu_vomtENRYh_Kxa0sgG1vkoo implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$n0uu_vomtENRYh_Kxa0sgG1vkoo(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$applyPeerLayer$8$SecretChatHelper(this.f$1);
    }
}
