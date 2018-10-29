package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class PaymentFormActivity$$Lambda$29 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TLObject arg$2;

    PaymentFormActivity$$Lambda$29(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$30$PaymentFormActivity(this.arg$2);
    }
}
