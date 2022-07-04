package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda78 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda78(SendMessagesHelper sendMessagesHelper, Runnable runnable) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m497lambda$sendReaction$22$orgtelegrammessengerSendMessagesHelper(this.f$1, tLObject, tL_error);
    }
}
