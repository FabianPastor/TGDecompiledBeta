package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$41 */
final /* synthetic */ class PaymentFormActivity$$Lambda$41 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    PaymentFormActivity$$Lambda$41(PaymentFormActivity paymentFormActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$23$PaymentFormActivity(this.arg$2, this.arg$3);
    }
}
