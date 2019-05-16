package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$y_-QKcAzTtKUDu-WddTe8KbVDxY implements Runnable {
    private final /* synthetic */ SecretChatHelper f$0;
    private final /* synthetic */ TL_encryptedChatDiscarded f$1;

    public /* synthetic */ -$$Lambda$SecretChatHelper$y_-QKcAzTtKUDu-WddTe8KbVDxY(SecretChatHelper secretChatHelper, TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
        this.f$0 = secretChatHelper;
        this.f$1 = tL_encryptedChatDiscarded;
    }

    public final void run() {
        this.f$0.lambda$decryptMessage$16$SecretChatHelper(this.f$1);
    }
}
