package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$24 */
final /* synthetic */ class PaymentFormActivity$$Lambda$24 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final String arg$2;
    private final TL_account_getPassword arg$3;

    PaymentFormActivity$$Lambda$24(PaymentFormActivity paymentFormActivity, String str, TL_account_getPassword tL_account_getPassword) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = str;
        this.arg$3 = tL_account_getPassword;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkPassword$45$PaymentFormActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
