package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.StickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$MR-_IfjNzjBKXtUZL0HU2iBd5uI implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ StickerSet f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$MR-_IfjNzjBKXtUZL0HU2iBd5uI(MediaDataController mediaDataController, StickerSet stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
    }

    public final void run() {
        this.f$0.lambda$loadGroupStickerSet$8$MediaDataController(this.f$1);
    }
}
