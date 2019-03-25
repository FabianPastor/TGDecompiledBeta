package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$85 implements RequestDelegate {
    private final DataQuery arg$1;
    private final long arg$2;

    DataQuery$$Lambda$85(DataQuery dataQuery, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$101$DataQuery(this.arg$2, tLObject, tL_error);
    }
}
