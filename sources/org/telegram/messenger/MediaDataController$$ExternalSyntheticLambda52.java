package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda52 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda52(MediaDataController mediaDataController, String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
        this.f$2 = tLRPC$TL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDiceStickers$45(this.f$1, this.f$2);
    }
}
