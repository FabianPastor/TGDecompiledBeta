package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda181 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda181 INSTANCE = new MediaDataController$$ExternalSyntheticLambda181();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda181() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersAsRead$49(tLObject, tLRPC$TL_error);
    }
}
