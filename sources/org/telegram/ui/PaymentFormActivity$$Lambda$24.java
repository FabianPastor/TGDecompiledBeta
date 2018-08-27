package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$24 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final TL_account_getTmpPassword arg$2;

    PaymentFormActivity$$Lambda$24(PaymentFormActivity paymentFormActivity, TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_account_getTmpPassword;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$37$PaymentFormActivity(this.arg$2, tLObject, tL_error);
    }
}
