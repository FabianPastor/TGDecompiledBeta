package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda166 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$StickerSet f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda166(MediaDataController mediaDataController, TLRPC$StickerSet tLRPC$StickerSet, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$StickerSet;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$toggleStickerSetInternal$81(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
