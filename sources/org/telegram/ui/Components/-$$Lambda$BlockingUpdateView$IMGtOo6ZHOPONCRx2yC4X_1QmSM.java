package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BlockingUpdateView$IMGtOo6ZHOPONCRx2yC4X_1QmSM implements RequestDelegate {
    private final /* synthetic */ BlockingUpdateView f$0;

    public /* synthetic */ -$$Lambda$BlockingUpdateView$IMGtOo6ZHOPONCRx2yC4X_1QmSM(BlockingUpdateView blockingUpdateView) {
        this.f$0 = blockingUpdateView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$show$4$BlockingUpdateView(tLObject, tL_error);
    }
}
