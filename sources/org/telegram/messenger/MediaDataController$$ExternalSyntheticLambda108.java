package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda108 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda108(MediaDataController mediaDataController, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$putSetToCache$34(this.f$1);
    }
}
