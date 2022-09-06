package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda197 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$InputStickerSet f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda197(MediaDataController mediaDataController, TLRPC$InputStickerSet tLRPC$InputStickerSet, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$InputStickerSet;
        this.f$2 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getStickerSet$29(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
