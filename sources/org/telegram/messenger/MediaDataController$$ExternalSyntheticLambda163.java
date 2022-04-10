package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda163 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda163 INSTANCE = new MediaDataController$$ExternalSyntheticLambda163();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda163() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$105(tLObject, tLRPC$TL_error);
    }
}
