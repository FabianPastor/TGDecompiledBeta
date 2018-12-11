package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

final /* synthetic */ class DataQuery$$Lambda$108 implements Runnable {
    private final DataQuery arg$1;
    private final TL_messages_stickerSet arg$2;

    DataQuery$$Lambda$108(DataQuery dataQuery, TL_messages_stickerSet tL_messages_stickerSet) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_messages_stickerSet;
    }

    public void run() {
        this.arg$1.lambda$null$7$DataQuery(this.arg$2);
    }
}
