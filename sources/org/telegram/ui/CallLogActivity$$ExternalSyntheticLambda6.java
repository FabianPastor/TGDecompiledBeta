package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ CallLogActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda6(CallLogActivity callLogActivity, boolean z) {
        this.f$0 = callLogActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$deleteAllMessages$6(this.f$1, tLObject, tLRPC$TL_error);
    }
}
