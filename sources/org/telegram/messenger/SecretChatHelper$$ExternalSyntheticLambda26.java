package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda26 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda26(SecretChatHelper secretChatHelper, long j) {
        this.f$0 = secretChatHelper;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$declineSecretChat$20(this.f$1, tLObject, tLRPC$TL_error);
    }
}
