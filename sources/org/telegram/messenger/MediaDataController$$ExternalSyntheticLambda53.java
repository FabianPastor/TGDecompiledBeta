package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda53 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda53 INSTANCE = new MediaDataController$$ExternalSyntheticLambda53();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda53() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removePeer$97(tLObject, tL_error);
    }
}
