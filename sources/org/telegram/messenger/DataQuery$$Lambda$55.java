package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$55 implements RequestDelegate {
    private final DataQuery arg$1;
    private final int arg$2;

    DataQuery$$Lambda$55(DataQuery dataQuery, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadPinnedMessageInternal$87$DataQuery(this.arg$2, tLObject, tL_error);
    }
}
