package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda51 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda51 INSTANCE = new MediaDataController$$ExternalSyntheticLambda51();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda51() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$31(tLObject, tL_error);
    }
}
