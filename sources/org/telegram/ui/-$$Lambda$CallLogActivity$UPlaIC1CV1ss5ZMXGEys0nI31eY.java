package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CallLogActivity$UPlaIC1CV1ss5ZMXGEys0nI31eY implements Runnable {
    private final /* synthetic */ CallLogActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$CallLogActivity$UPlaIC1CV1ss5ZMXGEys0nI31eY(CallLogActivity callLogActivity, TL_error tL_error, TLObject tLObject) {
        this.f$0 = callLogActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$5$CallLogActivity(this.f$1, this.f$2);
    }
}
