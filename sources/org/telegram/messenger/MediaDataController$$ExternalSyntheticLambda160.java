package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda160 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda160 INSTANCE = new MediaDataController$$ExternalSyntheticLambda160();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda160() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$40(tLObject, tLRPC$TL_error);
    }
}
