package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$33 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TL_error arg$2;
    private final boolean arg$3;
    private final TLObject arg$4;
    private final String arg$5;

    PaymentFormActivity$$Lambda$33(PaymentFormActivity paymentFormActivity, TL_error tL_error, boolean z, TLObject tLObject, String str) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_error;
        this.arg$3 = z;
        this.arg$4 = tLObject;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$null$26$PaymentFormActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
