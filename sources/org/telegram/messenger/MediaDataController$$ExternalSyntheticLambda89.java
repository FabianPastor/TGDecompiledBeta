package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$StickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$StickerSet f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda89(MediaDataController mediaDataController, TLRPC$StickerSet tLRPC$StickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$StickerSet;
    }

    public final void run() {
        this.f$0.lambda$loadGroupStickerSet$30(this.f$1);
    }
}
