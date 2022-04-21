package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda68 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda68 INSTANCE = new MediaDataController$$ExternalSyntheticLambda68();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda68() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$39(tLObject, tL_error);
    }
}
