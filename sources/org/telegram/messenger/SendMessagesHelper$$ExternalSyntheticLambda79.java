package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda79 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda79(SendMessagesHelper sendMessagesHelper, long j) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendGame$31(this.f$1, tLObject, tLRPC$TL_error);
    }
}