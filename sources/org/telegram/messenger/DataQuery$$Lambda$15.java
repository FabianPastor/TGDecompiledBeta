package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;

final /* synthetic */ class DataQuery$$Lambda$15 implements RequestDelegate {
    private final DataQuery arg$1;
    private final TL_messages_getFeaturedStickers arg$2;

    DataQuery$$Lambda$15(DataQuery dataQuery, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_messages_getFeaturedStickers;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadFeaturedStickers$19$DataQuery(this.arg$2, tLObject, tL_error);
    }
}
