package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class DataQuery$$Lambda$105 implements Runnable {
    private final DataQuery arg$1;
    private final TL_error arg$2;
    private final StickerSet arg$3;
    private final Context arg$4;
    private final int arg$5;

    DataQuery$$Lambda$105(DataQuery dataQuery, TL_error tL_error, StickerSet stickerSet, Context context, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_error;
        this.arg$3 = stickerSet;
        this.arg$4 = context;
        this.arg$5 = i;
    }

    public void run() {
        this.arg$1.lambda$null$45$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
