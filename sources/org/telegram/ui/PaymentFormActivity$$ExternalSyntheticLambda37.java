package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda37(PaymentFormActivity paymentFormActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m4180lambda$sendForm$53$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2);
    }
}
