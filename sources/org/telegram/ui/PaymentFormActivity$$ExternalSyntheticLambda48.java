package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda48 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda48(PaymentFormActivity paymentFormActivity, boolean z) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3515lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
