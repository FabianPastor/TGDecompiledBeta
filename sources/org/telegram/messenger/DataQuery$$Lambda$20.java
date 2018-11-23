package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$20 implements RequestDelegate {
    static final RequestDelegate $instance = new DataQuery$$Lambda$20();

    private DataQuery$$Lambda$20() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        DataQuery.lambda$markFaturedStickersByIdAsRead$27$DataQuery(tLObject, tL_error);
    }
}
