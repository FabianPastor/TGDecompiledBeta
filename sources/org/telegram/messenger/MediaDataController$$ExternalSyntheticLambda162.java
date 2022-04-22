package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda162 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda162 INSTANCE = new MediaDataController$$ExternalSyntheticLambda162();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda162() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$106(tLObject, tLRPC$TL_error);
    }
}
