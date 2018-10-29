package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$19 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final TLObject arg$2;

    PaymentFormActivity$$Lambda$19(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tLObject;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendForm$32$PaymentFormActivity(this.arg$2, tLObject, tL_error);
    }
}
