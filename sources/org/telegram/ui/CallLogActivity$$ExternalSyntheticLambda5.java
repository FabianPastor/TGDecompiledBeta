package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class CallLogActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ CallLogActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ CallLogActivity$$ExternalSyntheticLambda5(CallLogActivity callLogActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = callLogActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2748lambda$getCalls$8$orgtelegramuiCallLogActivity(this.f$1, this.f$2);
    }
}
