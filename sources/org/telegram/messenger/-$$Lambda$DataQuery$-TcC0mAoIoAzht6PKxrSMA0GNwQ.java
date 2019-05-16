package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.StickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$-TcC0mAoIoAzht6PKxrSMA0GNwQ implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ StickerSet f$1;

    public /* synthetic */ -$$Lambda$DataQuery$-TcC0mAoIoAzht6PKxrSMA0GNwQ(DataQuery dataQuery, StickerSet stickerSet) {
        this.f$0 = dataQuery;
        this.f$1 = stickerSet;
    }

    public final void run() {
        this.f$0.lambda$loadGroupStickerSet$6$DataQuery(this.f$1);
    }
}
