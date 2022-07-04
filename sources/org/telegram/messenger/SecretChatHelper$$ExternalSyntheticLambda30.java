package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda30 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC$EncryptedChat f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda30(SecretChatHelper secretChatHelper, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = tLRPC$EncryptedChat;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$acceptSecretChat$23(this.f$1, tLObject, tLRPC$TL_error);
    }
}
