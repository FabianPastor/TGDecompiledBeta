package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda112 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;
    public final /* synthetic */ TLRPC$InputStickerSet f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda112(MediaDataController mediaDataController, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_messages_stickerSet;
        this.f$2 = tLRPC$InputStickerSet;
    }

    public final void run() {
        this.f$0.lambda$getStickerSet$28(this.f$1, this.f$2);
    }
}
