package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WebviewActivity$O62eLYvTahA2PRe6UItNx5NI6ao implements RequestDelegate {
    private final /* synthetic */ WebviewActivity f$0;

    public /* synthetic */ -$$Lambda$WebviewActivity$O62eLYvTahA2PRe6UItNx5NI6ao(WebviewActivity webviewActivity) {
        this.f$0 = webviewActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$reloadStats$1$WebviewActivity(tLObject, tL_error);
    }
}
