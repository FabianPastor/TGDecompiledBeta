package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;

final /* synthetic */ class SecretChatHelper$$Lambda$8 implements Runnable {
    private final SecretChatHelper arg$1;
    private final TL_encryptedChatDiscarded arg$2;

    SecretChatHelper$$Lambda$8(SecretChatHelper secretChatHelper, TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
        this.arg$1 = secretChatHelper;
        this.arg$2 = tL_encryptedChatDiscarded;
    }

    public void run() {
        this.arg$1.lambda$decryptMessage$16$SecretChatHelper(this.arg$2);
    }
}
