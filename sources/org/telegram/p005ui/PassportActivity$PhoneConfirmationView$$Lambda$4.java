package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$$Lambda$4 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$4 implements RequestDelegate {
    static final RequestDelegate $instance = new PassportActivity$PhoneConfirmationView$$Lambda$4();

    private PassportActivity$PhoneConfirmationView$$Lambda$4() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        PhoneConfirmationView.lambda$onBackPressed$7$PassportActivity$PhoneConfirmationView(tLObject, tL_error);
    }
}
