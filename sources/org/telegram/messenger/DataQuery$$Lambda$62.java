package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$62 implements RequestDelegate {
    private final DataQuery arg$1;

    DataQuery$$Lambda$62(DataQuery dataQuery) {
        this.arg$1 = dataQuery;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadDrafts$98$DataQuery(tLObject, tL_error);
    }
}
