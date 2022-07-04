package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class WebviewActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ WebviewActivity f$0;

    public /* synthetic */ WebviewActivity$$ExternalSyntheticLambda1(WebviewActivity webviewActivity) {
        this.f$0 = webviewActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$reloadStats$1(tLObject, tLRPC$TL_error);
    }
}
