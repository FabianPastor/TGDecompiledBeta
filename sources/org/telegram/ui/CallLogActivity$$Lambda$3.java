package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class CallLogActivity$$Lambda$3 implements RequestDelegate {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$3(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getCalls$6$CallLogActivity(tLObject, tL_error);
    }
}
