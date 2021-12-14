package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_changePhone;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ ChangePhoneActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC$TL_account_changePhone f$1;

    public /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(ChangePhoneActivity.LoginActivitySmsView loginActivitySmsView, TLRPC$TL_account_changePhone tLRPC$TL_account_changePhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLRPC$TL_account_changePhone;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$7(this.f$1, tLObject, tLRPC$TL_error);
    }
}
