package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ ChangePhoneActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_account_changePhone f$1;

    public /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10(ChangePhoneActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_account_changePhone tL_account_changePhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_account_changePhone;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1553x367b511(this.f$1, tLObject, tL_error);
    }
}
