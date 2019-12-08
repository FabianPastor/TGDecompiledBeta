package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$22_VP_PsyLV189MEr2nl7-p4dOw implements RequestDelegate {
    private final /* synthetic */ LocationController f$0;

    public /* synthetic */ -$$Lambda$LocationController$22_VP_PsyLV189MEr2nl7-p4dOw(LocationController locationController) {
        this.f$0 = locationController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$markLiveLoactionsAsRead$25$LocationController(tLObject, tL_error);
    }
}
