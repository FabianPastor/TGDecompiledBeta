package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda155 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda155 INSTANCE = new MediaDataController$$ExternalSyntheticLambda155();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda155() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$35(tLObject, tLRPC$TL_error);
    }
}
