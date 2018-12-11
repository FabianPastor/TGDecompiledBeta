package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$30 implements RequestDelegate {
    private final DataQuery arg$1;
    private final StickerSet arg$2;
    private final Context arg$3;
    private final int arg$4;

    DataQuery$$Lambda$30(DataQuery dataQuery, StickerSet stickerSet, Context context, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = stickerSet;
        this.arg$3 = context;
        this.arg$4 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$removeStickersSet$46$DataQuery(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
