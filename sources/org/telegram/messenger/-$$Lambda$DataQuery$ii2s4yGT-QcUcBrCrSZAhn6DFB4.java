package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$ii2s4yGT-QcUcBrCrSZAhn6DFB4 implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$DataQuery$ii2s4yGT-QcUcBrCrSZAhn6DFB4(DataQuery dataQuery, TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = dataQuery;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$29$DataQuery(this.f$1, this.f$2, this.f$3);
    }
}
