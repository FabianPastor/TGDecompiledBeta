package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$34 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final boolean arg$2;

    PaymentFormActivity$$Lambda$34(PaymentFormActivity paymentFormActivity, boolean z) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$24$PaymentFormActivity(this.arg$2, tLObject, tL_error);
    }
}
