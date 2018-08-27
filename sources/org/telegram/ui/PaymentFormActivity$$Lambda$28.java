package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;

final /* synthetic */ class PaymentFormActivity$$Lambda$28 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TL_error arg$2;
    private final TL_payments_sendPaymentForm arg$3;

    PaymentFormActivity$$Lambda$28(PaymentFormActivity paymentFormActivity, TL_error tL_error, TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tL_payments_sendPaymentForm;
    }

    public void run() {
        this.arg$1.lambda$null$34$PaymentFormActivity(this.arg$2, this.arg$3);
    }
}
