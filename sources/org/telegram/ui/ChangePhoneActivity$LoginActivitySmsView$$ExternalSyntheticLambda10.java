package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public static final /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10 INSTANCE = new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10();

    private /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda10() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChangePhoneActivity.LoginActivitySmsView.lambda$onBackPressed$9(tLObject, tLRPC$TL_error);
    }
}
