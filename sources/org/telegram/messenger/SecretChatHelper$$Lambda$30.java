package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SecretChatHelper$$Lambda$30 implements Runnable {
    private final SecretChatHelper arg$1;
    private final Message arg$2;
    private final String arg$3;

    SecretChatHelper$$Lambda$30(SecretChatHelper secretChatHelper, Message message, String str) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = message;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$null$3$SecretChatHelper(this.arg$2, this.arg$3);
    }
}
