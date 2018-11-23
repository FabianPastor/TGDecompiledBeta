package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$10 implements RequestDelegate {
    private final DataQuery arg$1;
    private final int arg$2;
    private final boolean arg$3;

    DataQuery$$Lambda$10(DataQuery dataQuery, int i, boolean z) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadRecents$13$DataQuery(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
