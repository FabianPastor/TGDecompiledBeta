package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$6 implements RequestDelegate {
    private final DataQuery arg$1;

    DataQuery$$Lambda$6(DataQuery dataQuery) {
        this.arg$1 = dataQuery;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadGroupStickerSet$8$DataQuery(tLObject, tL_error);
    }
}
