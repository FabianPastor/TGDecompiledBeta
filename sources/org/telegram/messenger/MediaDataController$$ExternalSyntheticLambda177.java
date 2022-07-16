package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda177 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda177 INSTANCE = new MediaDataController$$ExternalSyntheticLambda177();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda177() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$50(tLObject, tLRPC$TL_error);
    }
}
