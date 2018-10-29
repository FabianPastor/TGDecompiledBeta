package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

final /* synthetic */ class SecretChatHelper$$Lambda$6 implements Runnable {
    private final SecretChatHelper arg$1;
    private final int arg$2;
    private final EncryptedChat arg$3;
    private final int arg$4;

    SecretChatHelper$$Lambda$6(SecretChatHelper secretChatHelper, int i, EncryptedChat encryptedChat, int i2) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = i;
        this.arg$3 = encryptedChat;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$resendMessages$14$SecretChatHelper(this.arg$2, this.arg$3, this.arg$4);
    }
}
