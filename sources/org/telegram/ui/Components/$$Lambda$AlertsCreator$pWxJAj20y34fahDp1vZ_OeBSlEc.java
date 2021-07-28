package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc INSTANCE = new $$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc();

    private /* synthetic */ $$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$createChangeBioAlert$20(tLObject, tLRPC$TL_error);
    }
}
