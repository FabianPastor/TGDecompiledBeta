package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda146 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda146 INSTANCE = new MediaDataController$$ExternalSyntheticLambda146();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda146() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$31(tLObject, tLRPC$TL_error);
    }
}
