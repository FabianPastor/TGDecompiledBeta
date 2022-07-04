package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda168 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.StickerSet f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC.TL_messages_stickerSet f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda168(MediaDataController mediaDataController, TLRPC.StickerSet stickerSet, int i, int i2, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.m2110xd5e9dfa4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
