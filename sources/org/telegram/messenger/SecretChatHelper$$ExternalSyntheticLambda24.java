package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda24 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ TLRPC.EncryptedChat f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda24(SecretChatHelper secretChatHelper, TLRPC.EncryptedChat encryptedChat) {
        this.f$0 = secretChatHelper;
        this.f$1 = encryptedChat;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1130xb735687(this.f$1, tLObject, tL_error);
    }
}
