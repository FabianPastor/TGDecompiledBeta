package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;

final /* synthetic */ class DataQuery$$Lambda$113 implements Runnable {
    private final DataQuery arg$1;
    private final TLObject arg$2;
    private final ArrayList arg$3;
    private final int arg$4;
    private final LongSparseArray arg$5;
    private final StickerSet arg$6;
    private final TL_messages_allStickers arg$7;
    private final int arg$8;

    DataQuery$$Lambda$113(DataQuery dataQuery, TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = tLObject;
        this.arg$3 = arrayList;
        this.arg$4 = i;
        this.arg$5 = longSparseArray;
        this.arg$6 = stickerSet;
        this.arg$7 = tL_messages_allStickers;
        this.arg$8 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$31$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
