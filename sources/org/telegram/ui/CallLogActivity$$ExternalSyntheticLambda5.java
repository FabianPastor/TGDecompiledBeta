package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ CallLogActivity f$0;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda5(CallLogActivity callLogActivity) {
        this.f$0 = callLogActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getCalls$9(tLObject, tLRPC$TL_error);
    }
}
