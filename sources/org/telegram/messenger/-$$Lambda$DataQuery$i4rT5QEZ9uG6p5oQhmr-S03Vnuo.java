package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$i4rT5QEZ9uG6p5oQhmr-S03Vnuo implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$DataQuery$i4rT5QEZ9uG6p5oQhmr-S03Vnuo(DataQuery dataQuery, int[] iArr, int i, long j) {
        this.f$0 = dataQuery;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$54$DataQuery(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
