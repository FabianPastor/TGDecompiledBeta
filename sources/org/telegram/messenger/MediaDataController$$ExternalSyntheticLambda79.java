package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda79 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda79 INSTANCE = new MediaDataController$$ExternalSyntheticLambda79();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda79() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$47(tLObject, tL_error);
    }
}
