package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;

final /* synthetic */ class DataQuery$$Lambda$2 implements RequestDelegate {
    private final DataQuery arg$1;
    private final TL_messages_saveGif arg$2;

    DataQuery$$Lambda$2(DataQuery dataQuery, TL_messages_saveGif tL_messages_saveGif) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_messages_saveGif;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$removeRecentGif$2$DataQuery(this.arg$2, tLObject, tL_error);
    }
}
