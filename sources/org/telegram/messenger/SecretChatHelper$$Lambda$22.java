package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

final /* synthetic */ class SecretChatHelper$$Lambda$22 implements Runnable {
    private final SecretChatHelper arg$1;
    private final EncryptedChat arg$2;

    SecretChatHelper$$Lambda$22(SecretChatHelper secretChatHelper, EncryptedChat encryptedChat) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = encryptedChat;
    }

    public void run() {
        this.arg$1.lambda$null$20$SecretChatHelper(this.arg$2);
    }
}
