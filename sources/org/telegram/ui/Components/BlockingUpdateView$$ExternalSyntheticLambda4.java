package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BlockingUpdateView$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ BlockingUpdateView f$0;

    public /* synthetic */ BlockingUpdateView$$ExternalSyntheticLambda4(BlockingUpdateView blockingUpdateView) {
        this.f$0 = blockingUpdateView;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2018lambda$show$4$orgtelegramuiComponentsBlockingUpdateView(tLObject, tL_error);
    }
}
