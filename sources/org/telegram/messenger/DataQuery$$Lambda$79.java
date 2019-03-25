package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$79 implements RequestDelegate {
    private final DataQuery arg$1;
    private final int arg$2;
    private final String arg$3;
    private final String arg$4;

    DataQuery$$Lambda$79(DataQuery dataQuery, int i, String str, String str2) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = str2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$115$DataQuery(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
