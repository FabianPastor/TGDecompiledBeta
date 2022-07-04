package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda80 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda80 INSTANCE = new MediaDataController$$ExternalSyntheticLambda80();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda80() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$48(tLObject, tL_error);
    }
}
