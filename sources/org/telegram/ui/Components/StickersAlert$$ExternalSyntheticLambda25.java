package org.telegram.ui.Components;

import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ MediaDataController f$3;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda25(StickersAlert stickersAlert, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MediaDataController mediaDataController) {
        this.f$0 = stickersAlert;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = mediaDataController;
    }

    public final void run() {
        this.f$0.lambda$loadStickerSet$5(this.f$1, this.f$2, this.f$3);
    }
}
