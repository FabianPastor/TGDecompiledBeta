package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda126 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean[] f$1;
    public final /* synthetic */ TLRPC$StickerSet f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$5;
    public final /* synthetic */ Runnable f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda126(MediaDataController mediaDataController, boolean[] zArr, TLRPC$StickerSet tLRPC$StickerSet, int i, int i2, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = zArr;
        this.f$2 = tLRPC$StickerSet;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = tLRPC$TL_messages_stickerSet;
        this.f$6 = runnable;
    }

    public final void run() {
        this.f$0.lambda$toggleStickerSet$86(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
