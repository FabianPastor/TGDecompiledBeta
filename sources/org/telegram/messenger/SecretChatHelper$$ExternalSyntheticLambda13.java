package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$Dialog f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda13(SecretChatHelper secretChatHelper, TLRPC$Dialog tLRPC$Dialog, long j) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$Dialog;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$processUpdateEncryption$1(this.f$1, this.f$2);
    }
}
