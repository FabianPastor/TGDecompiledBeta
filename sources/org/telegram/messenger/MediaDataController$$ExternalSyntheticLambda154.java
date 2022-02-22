package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda154 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda154 INSTANCE = new MediaDataController$$ExternalSyntheticLambda154();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda154() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$102(tLObject, tLRPC$TL_error);
    }
}
