package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$35 */
final /* synthetic */ class PaymentFormActivity$$Lambda$35 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final boolean arg$2;
    private final String arg$3;

    PaymentFormActivity$$Lambda$35(PaymentFormActivity paymentFormActivity, boolean z, String str) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = z;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$32$PaymentFormActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
