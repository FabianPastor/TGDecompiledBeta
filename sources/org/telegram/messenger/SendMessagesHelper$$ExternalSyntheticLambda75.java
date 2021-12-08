package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda75 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda75(SendMessagesHelper sendMessagesHelper) {
        this.f$0 = sendMessagesHelper;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m478lambda$sendReaction$22$orgtelegrammessengerSendMessagesHelper(tLObject, tL_error);
    }
}
