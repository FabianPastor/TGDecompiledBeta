package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;

final /* synthetic */ class DataQuery$$Lambda$23 implements RequestDelegate {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final LongSparseArray arg$4;
    private final StickerSet arg$5;
    private final TL_messages_allStickers arg$6;
    private final int arg$7;

    DataQuery$$Lambda$23(DataQuery dataQuery, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = longSparseArray;
        this.arg$5 = stickerSet;
        this.arg$6 = tL_messages_allStickers;
        this.arg$7 = i2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$processLoadStickersResponse$32$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
