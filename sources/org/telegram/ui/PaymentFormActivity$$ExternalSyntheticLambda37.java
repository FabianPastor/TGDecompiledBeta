package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_payments_sendPaymentForm f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda37(PaymentFormActivity paymentFormActivity, TLRPC.TL_error tL_error, TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_payments_sendPaymentForm;
    }

    public final void run() {
        this.f$0.m3507lambda$sendData$52$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2);
    }
}
