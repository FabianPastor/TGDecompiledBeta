package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda52 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda52 INSTANCE = new MediaDataController$$ExternalSyntheticLambda52();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda52() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removeInline$96(tLObject, tL_error);
    }
}
