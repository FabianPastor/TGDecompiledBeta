package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ CallLogActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda7(CallLogActivity callLogActivity, boolean z) {
        this.f$0 = callLogActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1493lambda$deleteAllMessages$6$orgtelegramuiCallLogActivity(this.f$1, tLObject, tL_error);
    }
}
