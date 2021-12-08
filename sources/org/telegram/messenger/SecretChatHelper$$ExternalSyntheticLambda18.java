package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda18 implements RequestDelegate {
    public final /* synthetic */ SecretChatHelper f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda18(SecretChatHelper secretChatHelper, long j) {
        this.f$0 = secretChatHelper;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1132xbee5b370(this.f$1, tLObject, tL_error);
    }
}
