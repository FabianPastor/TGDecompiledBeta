package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_stickers_createStickerSet;

public final /* synthetic */ class SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.ImportingStickers.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_stickers_createStickerSet f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0(SendMessagesHelper.ImportingStickers.AnonymousClass1 r1, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_stickers_createStickerSet;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$run$0(this.f$1, this.f$2, this.f$3);
    }
}
