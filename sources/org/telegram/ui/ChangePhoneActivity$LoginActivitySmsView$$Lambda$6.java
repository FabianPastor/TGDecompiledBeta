package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$6 implements RequestDelegate {
    static final RequestDelegate $instance = new ChangePhoneActivity$LoginActivitySmsView$$Lambda$6();

    private ChangePhoneActivity$LoginActivitySmsView$$Lambda$6() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        LoginActivitySmsView.lambda$onBackPressed$9$ChangePhoneActivity$LoginActivitySmsView(tLObject, tL_error);
    }
}
