package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda153 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda153 INSTANCE = new MediaDataController$$ExternalSyntheticLambda153();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda153() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$34(tLObject, tLRPC$TL_error);
    }
}
