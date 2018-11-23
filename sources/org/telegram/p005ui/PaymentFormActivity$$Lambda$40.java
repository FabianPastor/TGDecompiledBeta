package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$40 */
final /* synthetic */ class PaymentFormActivity$$Lambda$40 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TL_error arg$2;

    PaymentFormActivity$$Lambda$40(PaymentFormActivity paymentFormActivity, TL_error tL_error) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$26$PaymentFormActivity(this.arg$2);
    }
}
