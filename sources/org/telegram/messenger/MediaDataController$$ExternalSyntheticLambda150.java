package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda150 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda150 INSTANCE = new MediaDataController$$ExternalSyntheticLambda150();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda150() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$saveDraft$128(tLObject, tLRPC$TL_error);
    }
}
