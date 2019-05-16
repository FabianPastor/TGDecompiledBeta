package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$x29XwwId3_CQasQvc9QUrJecg8c implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ StickerSet f$2;
    private final /* synthetic */ Context f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$DataQuery$x29XwwId3_CQasQvc9QUrJecg8c(DataQuery dataQuery, TL_error tL_error, StickerSet stickerSet, Context context, int i) {
        this.f$0 = dataQuery;
        this.f$1 = tL_error;
        this.f$2 = stickerSet;
        this.f$3 = context;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.lambda$null$45$DataQuery(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
