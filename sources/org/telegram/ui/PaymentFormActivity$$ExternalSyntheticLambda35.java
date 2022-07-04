package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda35(PaymentFormActivity paymentFormActivity, TLRPC.TL_error tL_error) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m4182lambda$sendSavePassword$40$orgtelegramuiPaymentFormActivity(this.f$1);
    }
}
