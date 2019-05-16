package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass8;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$8$mGdIMbtzY2Oz5TSSjAmA4uEPy4U implements RequestDelegate {
    private final /* synthetic */ AnonymousClass8 f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$8$mGdIMbtzY2Oz5TSSjAmA4uEPy4U(AnonymousClass8 anonymousClass8, boolean z) {
        this.f$0 = anonymousClass8;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$10$PassportActivity$8(this.f$1, tLObject, tL_error);
    }
}
