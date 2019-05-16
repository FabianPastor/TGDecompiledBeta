package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$kdOc-KDCO9lesbolp8AlwqF1nh4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$kdOc-KDCO9lesbolp8AlwqF1nh4 INSTANCE = new -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$kdOc-KDCO9lesbolp8AlwqF1nh4();

    private /* synthetic */ -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$kdOc-KDCO9lesbolp8AlwqF1nh4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        LoginActivitySmsView.lambda$onBackPressed$9(tLObject, tL_error);
    }
}
