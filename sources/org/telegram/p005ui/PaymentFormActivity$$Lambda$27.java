package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$27 */
final /* synthetic */ class PaymentFormActivity$$Lambda$27 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final TL_account_getTmpPassword arg$2;

    PaymentFormActivity$$Lambda$27(PaymentFormActivity paymentFormActivity, TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_account_getTmpPassword;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$42$PaymentFormActivity(this.arg$2, tLObject, tL_error);
    }
}
