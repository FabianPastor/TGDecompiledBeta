package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda129 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.StickerSet f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda129(MediaDataController mediaDataController, TLRPC.StickerSet stickerSet, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m899xavar_b23(this.f$1, this.f$2);
    }
}
