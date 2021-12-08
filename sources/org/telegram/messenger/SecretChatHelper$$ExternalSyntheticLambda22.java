package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$TL_encryptedChatDiscarded f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda22(SecretChatHelper secretChatHelper, TLRPC$TL_encryptedChatDiscarded tLRPC$TL_encryptedChatDiscarded) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$TL_encryptedChatDiscarded;
    }

    public final void run() {
        this.f$0.lambda$decryptMessage$17(this.f$1);
    }
}
