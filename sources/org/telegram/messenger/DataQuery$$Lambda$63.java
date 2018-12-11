package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$63 implements RequestDelegate {
    static final RequestDelegate $instance = new DataQuery$$Lambda$63();

    private DataQuery$$Lambda$63() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        DataQuery.lambda$saveDraft$99$DataQuery(tLObject, tL_error);
    }
}
