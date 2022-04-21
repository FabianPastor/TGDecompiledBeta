package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda71 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda71 INSTANCE = new MediaDataController$$ExternalSyntheticLambda71();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda71() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removePeer$106(tLObject, tL_error);
    }
}
