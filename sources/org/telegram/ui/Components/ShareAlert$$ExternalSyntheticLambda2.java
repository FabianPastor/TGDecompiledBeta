package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ShareAlert$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ShareAlert f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ ShareAlert$$ExternalSyntheticLambda2(ShareAlert shareAlert, Context context) {
        this.f$0 = shareAlert;
        this.f$1 = context;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1350lambda$new$1$orgtelegramuiComponentsShareAlert(this.f$1, tLObject, tL_error);
    }
}
