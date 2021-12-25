package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda151 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda151 INSTANCE = new MediaDataController$$ExternalSyntheticLambda151();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda151() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$35(tLObject, tLRPC$TL_error);
    }
}
