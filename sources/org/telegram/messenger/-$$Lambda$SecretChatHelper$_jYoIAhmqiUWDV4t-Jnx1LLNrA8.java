package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$_jYoIAhmqiUWDV4t-Jnx1LLNrA8 implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ EncryptedChat f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$_jYoIAhmqiUWDV4t-Jnx1LLNrA8(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run() {
        this.f$0.lambda$null$20$SecretChatHelper(this.f$1);
    }
}
