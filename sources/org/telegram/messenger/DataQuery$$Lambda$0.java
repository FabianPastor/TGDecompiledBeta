package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;

final /* synthetic */ class DataQuery$$Lambda$0 implements RequestDelegate {
    private final DataQuery arg$1;
    private final Object arg$2;
    private final TL_messages_faveSticker arg$3;

    DataQuery$$Lambda$0(DataQuery dataQuery, Object obj, TL_messages_faveSticker tL_messages_faveSticker) {
        this.arg$1 = dataQuery;
        this.arg$2 = obj;
        this.arg$3 = tL_messages_faveSticker;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$addRecentSticker$0$DataQuery(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
