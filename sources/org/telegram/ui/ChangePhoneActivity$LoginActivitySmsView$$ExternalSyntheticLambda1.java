package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda1 INSTANCE = new ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda1();

    private /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda1() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChangePhoneActivity.LoginActivitySmsView.lambda$onBackPressed$9(tLObject, tL_error);
    }
}
