package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$30 */
final /* synthetic */ class PaymentFormActivity$$Lambda$30 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TLObject arg$2;

    PaymentFormActivity$$Lambda$30(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$38$PaymentFormActivity(this.arg$2);
    }
}
