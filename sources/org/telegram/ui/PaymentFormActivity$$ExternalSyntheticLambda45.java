package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda45 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda45(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3511lambda$sendForm$49$orgtelegramuiPaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
