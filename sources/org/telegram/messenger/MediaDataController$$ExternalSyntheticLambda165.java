package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda165 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda165 INSTANCE = new MediaDataController$$ExternalSyntheticLambda165();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda165() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$39(tLObject, tLRPC$TL_error);
    }
}
