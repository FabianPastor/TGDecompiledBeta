package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda206 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda206 INSTANCE = new MediaDataController$$ExternalSyntheticLambda206();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda206() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersAsRead$51(tLObject, tLRPC$TL_error);
    }
}
