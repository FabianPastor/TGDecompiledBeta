package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.SessionsActivity.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$2$H9BWqXWKkY_c8BLHuJ_V9Mr_9Ro implements RequestDelegate {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ TL_authorization f$1;

    public /* synthetic */ -$$Lambda$SessionsActivity$2$H9BWqXWKkY_c8BLHuJ_V9Mr_9Ro(AnonymousClass2 anonymousClass2, TL_authorization tL_authorization) {
        this.f$0 = anonymousClass2;
        this.f$1 = tL_authorization;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$hide$1$SessionsActivity$2(this.f$1, tLObject, tL_error);
    }
}
