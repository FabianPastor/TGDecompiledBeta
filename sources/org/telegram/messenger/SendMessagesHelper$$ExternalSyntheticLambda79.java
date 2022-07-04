package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda79 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda79(SendMessagesHelper sendMessagesHelper, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m495xcd8f7dd1(this.f$1, tLObject, tL_error);
    }
}
