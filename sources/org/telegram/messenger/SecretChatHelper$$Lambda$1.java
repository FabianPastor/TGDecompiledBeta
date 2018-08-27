package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_dialog;

final /* synthetic */ class SecretChatHelper$$Lambda$1 implements Runnable {
    private final SecretChatHelper arg$1;
    private final TL_dialog arg$2;

    SecretChatHelper$$Lambda$1(SecretChatHelper secretChatHelper, TL_dialog tL_dialog) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = tL_dialog;
    }

    public void run() {
        this.arg$1.lambda$processUpdateEncryption$1$SecretChatHelper(this.arg$2);
    }
}
