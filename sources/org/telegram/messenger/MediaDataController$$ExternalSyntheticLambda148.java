package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda148 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda148 INSTANCE = new MediaDataController$$ExternalSyntheticLambda148();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda148() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$30(tLObject, tLRPC$TL_error);
    }
}
