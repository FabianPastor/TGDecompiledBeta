package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class CallLogActivity$$Lambda$6 implements Runnable {
    private final CallLogActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    CallLogActivity$$Lambda$6(CallLogActivity callLogActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = callLogActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$5$CallLogActivity(this.arg$2, this.arg$3);
    }
}
