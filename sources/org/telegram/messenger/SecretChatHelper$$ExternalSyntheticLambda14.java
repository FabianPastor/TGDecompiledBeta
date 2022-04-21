package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.TL_encryptedChatDiscarded f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda14(SecretChatHelper secretChatHelper, TLRPC.TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
        this.f$0 = secretChatHelper;
        this.f$1 = tL_encryptedChatDiscarded;
    }

    public final void run() {
        this.f$0.m1075lambda$decryptMessage$17$orgtelegrammessengerSecretChatHelper(this.f$1);
    }
}
