package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRecoverView$rgaN2I5iElJnNYqU1iHy0vwcr6w implements RequestDelegate {
    private final /* synthetic */ LoginActivityRecoverView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRecoverView$rgaN2I5iElJnNYqU1iHy0vwcr6w(LoginActivityRecoverView loginActivityRecoverView) {
        this.f$0 = loginActivityRecoverView;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$5$LoginActivity$LoginActivityRecoverView(tLObject, tL_error);
    }
}
