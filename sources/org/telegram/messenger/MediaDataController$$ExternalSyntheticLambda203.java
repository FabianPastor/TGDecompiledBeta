package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda203 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda203 INSTANCE = new MediaDataController$$ExternalSyntheticLambda203();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda203() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$131(tLObject, tLRPC$TL_error);
    }
}
