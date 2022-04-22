package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BlockingUpdateView$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ BlockingUpdateView f$0;

    public /* synthetic */ BlockingUpdateView$$ExternalSyntheticLambda3(BlockingUpdateView blockingUpdateView) {
        this.f$0 = blockingUpdateView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$show$3(tLObject, tLRPC$TL_error);
    }
}
