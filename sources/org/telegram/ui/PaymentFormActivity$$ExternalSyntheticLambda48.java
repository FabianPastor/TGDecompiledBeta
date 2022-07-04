package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda48 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda48(PaymentFormActivity paymentFormActivity, Runnable runnable, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = runnable;
        this.f$2 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4192lambda$sendSavedForm$50$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
