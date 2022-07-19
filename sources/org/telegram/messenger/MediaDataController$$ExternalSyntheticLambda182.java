package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda182 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda182 INSTANCE = new MediaDataController$$ExternalSyntheticLambda182();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda182() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersByIdAsRead$50(tLObject, tLRPC$TL_error);
    }
}
