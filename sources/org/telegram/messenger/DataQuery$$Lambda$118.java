package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;

final /* synthetic */ class DataQuery$$Lambda$118 implements Runnable {
    private final DataQuery arg$1;
    private final TLObject arg$2;
    private final TL_messages_getFeaturedStickers arg$3;

    DataQuery$$Lambda$118(DataQuery dataQuery, TLObject tLObject, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.arg$1 = dataQuery;
        this.arg$2 = tLObject;
        this.arg$3 = tL_messages_getFeaturedStickers;
    }

    public void run() {
        this.arg$1.lambda$null$18$DataQuery(this.arg$2, this.arg$3);
    }
}
