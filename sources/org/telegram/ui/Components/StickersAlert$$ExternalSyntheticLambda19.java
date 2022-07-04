package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda19(StickersAlert stickersAlert) {
        this.f$0 = stickersAlert;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1456lambda$updateFields$15$orgtelegramuiComponentsStickersAlert(tLObject, tL_error);
    }
}
