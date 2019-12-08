package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.StickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$KjUTQcTigusm8drXcmGjjvKkBEY implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ StickerSet f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$KjUTQcTigusm8drXcmGjjvKkBEY(MediaDataController mediaDataController, StickerSet stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
    }

    public final void run() {
        this.f$0.lambda$loadGroupStickerSet$7$MediaDataController(this.f$1);
    }
}
