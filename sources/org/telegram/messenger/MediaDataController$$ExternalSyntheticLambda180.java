package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda180 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda180(MediaDataController mediaDataController, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getStickerSet$27(this.f$1, tLObject, tLRPC$TL_error);
    }
}
