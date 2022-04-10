package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda164 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda164 INSTANCE = new MediaDataController$$ExternalSyntheticLambda164();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda164() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$39(tLObject, tLRPC$TL_error);
    }
}
