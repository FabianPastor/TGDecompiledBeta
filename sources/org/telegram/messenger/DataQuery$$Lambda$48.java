package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$48 implements RequestDelegate {
    static final RequestDelegate $instance = new DataQuery$$Lambda$48();

    private DataQuery$$Lambda$48() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        DataQuery.lambda$removeInline$77$DataQuery(tLObject, tL_error);
    }
}
