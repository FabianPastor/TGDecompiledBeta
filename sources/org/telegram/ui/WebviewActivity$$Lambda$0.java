package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WebviewActivity$$Lambda$0 implements RequestDelegate {
    private final WebviewActivity arg$1;

    WebviewActivity$$Lambda$0(WebviewActivity webviewActivity) {
        this.arg$1 = webviewActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$reloadStats$1$WebviewActivity(tLObject, tL_error);
    }
}
