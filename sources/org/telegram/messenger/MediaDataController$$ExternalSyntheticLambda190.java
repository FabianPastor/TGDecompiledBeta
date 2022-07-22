package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda190 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda190 INSTANCE = new MediaDataController$$ExternalSyntheticLambda190();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda190() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$125(tLObject, tLRPC$TL_error);
    }
}
