package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda50 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda50 INSTANCE = new MediaDataController$$ExternalSyntheticLambda50();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda50() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$30(tLObject, tL_error);
    }
}
