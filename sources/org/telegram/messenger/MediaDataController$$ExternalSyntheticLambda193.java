package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda193 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda193 INSTANCE = new MediaDataController$$ExternalSyntheticLambda193();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda193() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersByIdAsRead$50(tLObject, tLRPC$TL_error);
    }
}
