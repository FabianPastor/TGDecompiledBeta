package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda189 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda189 INSTANCE = new MediaDataController$$ExternalSyntheticLambda189();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda189() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFeaturedStickersAsRead$49(tLObject, tLRPC$TL_error);
    }
}
