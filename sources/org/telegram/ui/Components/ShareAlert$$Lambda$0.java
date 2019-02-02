package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ShareAlert$$Lambda$0 implements RequestDelegate {
    private final ShareAlert arg$1;
    private final Context arg$2;

    ShareAlert$$Lambda$0(ShareAlert shareAlert, Context context) {
        this.arg$1 = shareAlert;
        this.arg$2 = context;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$new$1$ShareAlert(this.arg$2, tLObject, tL_error);
    }
}
