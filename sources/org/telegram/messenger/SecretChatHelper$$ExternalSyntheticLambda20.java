package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda20(SecretChatHelper secretChatHelper, TLRPC$Message tLRPC$Message, int i, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = i;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendEncryptedRequest$4(this.f$1, this.f$2, this.f$3);
    }
}
