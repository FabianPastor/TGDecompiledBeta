package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda195 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda195 INSTANCE = new MediaDataController$$ExternalSyntheticLambda195();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda195() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersByIdAsRead$51(tLObject, tLRPC$TL_error);
    }
}
