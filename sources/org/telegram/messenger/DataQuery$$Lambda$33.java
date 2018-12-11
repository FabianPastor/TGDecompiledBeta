package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$33 implements RequestDelegate {
    private final DataQuery arg$1;
    private final int arg$2;
    private final long arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;
    private final boolean arg$7;

    DataQuery$$Lambda$33(DataQuery dataQuery, int i, long j, int i2, int i3, int i4, boolean z) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = j;
        this.arg$4 = i2;
        this.arg$5 = i3;
        this.arg$6 = i4;
        this.arg$7 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadMedia$51$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
