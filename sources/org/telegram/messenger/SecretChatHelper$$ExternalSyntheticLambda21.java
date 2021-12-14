package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$messages_SentEncryptedMessage;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda21 implements Runnable {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ TLRPC$messages_SentEncryptedMessage f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda21(SecretChatHelper secretChatHelper, TLRPC$Message tLRPC$Message, TLRPC$messages_SentEncryptedMessage tLRPC$messages_SentEncryptedMessage, int i, String str) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = tLRPC$messages_SentEncryptedMessage;
        this.f$3 = i;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendEncryptedRequest$5(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
