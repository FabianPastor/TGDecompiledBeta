package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$nnb2Q4_79T9z1PWAXzjTLfJ8r2Q implements Runnable {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_account_changePhone f$3;

    public /* synthetic */ -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$nnb2Q4_79T9z1PWAXzjTLfJ8r2Q(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TLObject tLObject, TL_account_changePhone tL_account_changePhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_account_changePhone;
    }

    public final void run() {
        this.f$0.lambda$null$6$ChangePhoneActivity$LoginActivitySmsView(this.f$1, this.f$2, this.f$3);
    }
}
