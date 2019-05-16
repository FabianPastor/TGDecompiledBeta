package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$_a_ihjluIJ2vz2DCIrqa5SJZHuo implements RequestDelegate {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_account_changePhone f$1;

    public /* synthetic */ -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$_a_ihjluIJ2vz2DCIrqa5SJZHuo(LoginActivitySmsView loginActivitySmsView, TL_account_changePhone tL_account_changePhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_account_changePhone;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$7$ChangePhoneActivity$LoginActivitySmsView(this.f$1, tLObject, tL_error);
    }
}
