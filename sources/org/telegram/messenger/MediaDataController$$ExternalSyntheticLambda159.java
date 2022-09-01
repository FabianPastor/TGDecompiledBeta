package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda159 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda159(MediaDataController mediaDataController) {
        this.f$0 = mediaDataController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadHints$128(tLObject, tLRPC$TL_error);
    }
}
