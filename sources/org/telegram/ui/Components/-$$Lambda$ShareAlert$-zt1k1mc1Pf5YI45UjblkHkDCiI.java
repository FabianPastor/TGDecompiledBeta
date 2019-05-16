package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ShareAlert$-zt1k1mc1Pf5YI45UjblkHkDCiI implements RequestDelegate {
    private final /* synthetic */ ShareAlert f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$ShareAlert$-zt1k1mc1Pf5YI45UjblkHkDCiI(ShareAlert shareAlert, Context context) {
        this.f$0 = shareAlert;
        this.f$1 = context;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$new$1$ShareAlert(this.f$1, tLObject, tL_error);
    }
}
