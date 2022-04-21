package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda75 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda75(SendMessagesHelper sendMessagesHelper, long j) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m477lambda$sendGame$31$orgtelegrammessengerSendMessagesHelper(this.f$1, tLObject, tL_error);
    }
}
