package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;

final /* synthetic */ class PaymentFormActivity$$Lambda$20 implements RequestDelegate {
    private final PaymentFormActivity arg$1;
    private final TL_payments_sendPaymentForm arg$2;

    PaymentFormActivity$$Lambda$20(PaymentFormActivity paymentFormActivity, TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_payments_sendPaymentForm;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendData$35$PaymentFormActivity(this.arg$2, tLObject, tL_error);
    }
}
