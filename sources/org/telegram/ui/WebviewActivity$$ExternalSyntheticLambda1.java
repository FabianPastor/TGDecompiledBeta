package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class WebviewActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ WebviewActivity f$0;

    public /* synthetic */ WebviewActivity$$ExternalSyntheticLambda1(WebviewActivity webviewActivity) {
        this.f$0 = webviewActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4855lambda$reloadStats$1$orgtelegramuiWebviewActivity(tLObject, tL_error);
    }
}
