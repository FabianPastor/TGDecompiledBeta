package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ StickersActivity f$0;

    public /* synthetic */ StickersActivity$$ExternalSyntheticLambda3(StickersActivity stickersActivity) {
        this.f$0 = stickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3902lambda$sendReorder$5$orgtelegramuiStickersActivity(tLObject, tL_error);
    }
}
