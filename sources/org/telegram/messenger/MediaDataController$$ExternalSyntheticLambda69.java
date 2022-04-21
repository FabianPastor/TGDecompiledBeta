package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda69 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda69 INSTANCE = new MediaDataController$$ExternalSyntheticLambda69();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda69() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$40(tLObject, tL_error);
    }
}
