package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$35 implements RequestDelegate {
    private final DataQuery arg$1;
    private final long arg$2;
    private final int arg$3;
    private final int arg$4;

    DataQuery$$Lambda$35(DataQuery dataQuery, long j, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getMediaCount$58$DataQuery(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
