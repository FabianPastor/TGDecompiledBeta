package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda174 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda174 INSTANCE = new MediaDataController$$ExternalSyntheticLambda174();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda174() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$48(tLObject, tLRPC$TL_error);
    }
}
