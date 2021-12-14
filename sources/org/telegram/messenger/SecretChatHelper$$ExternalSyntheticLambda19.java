package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda19 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda19(SecretChatHelper secretChatHelper, TLRPC$Message tLRPC$Message) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$performSendEncryptedRequest$6(this.f$1);
    }
}
