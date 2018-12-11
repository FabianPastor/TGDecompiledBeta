package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.StickerSet;

final /* synthetic */ class DataQuery$$Lambda$5 implements Runnable {
    private final DataQuery arg$1;
    private final StickerSet arg$2;

    DataQuery$$Lambda$5(DataQuery dataQuery, StickerSet stickerSet) {
        this.arg$1 = dataQuery;
        this.arg$2 = stickerSet;
    }

    public void run() {
        this.arg$1.lambda$loadGroupStickerSet$6$DataQuery(this.arg$2);
    }
}
