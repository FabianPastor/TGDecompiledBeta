package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda156 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda156 INSTANCE = new MediaDataController$$ExternalSyntheticLambda156();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda156() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$100(tLObject, tLRPC$TL_error);
    }
}
