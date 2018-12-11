package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$20 */
final /* synthetic */ class PaymentFormActivity$$Lambda$20 implements RequestDelegate {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$20(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendSavePassword$27$PaymentFormActivity(tLObject, tL_error);
    }
}
