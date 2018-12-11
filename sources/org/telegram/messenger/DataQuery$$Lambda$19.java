package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$19 implements RequestDelegate {
    static final RequestDelegate $instance = new DataQuery$$Lambda$19();

    private DataQuery$$Lambda$19() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        DataQuery.lambda$markFaturedStickersAsRead$26$DataQuery(tLObject, tL_error);
    }
}
