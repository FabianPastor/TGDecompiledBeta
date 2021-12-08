package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ ChangePhoneActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_account_changePhone f$3;

    public /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(ChangePhoneActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_account_changePhone tL_account_changePhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_account_changePhone;
    }

    public final void run() {
        this.f$0.m1579x767a9df2(this.f$1, this.f$2, this.f$3);
    }
}
