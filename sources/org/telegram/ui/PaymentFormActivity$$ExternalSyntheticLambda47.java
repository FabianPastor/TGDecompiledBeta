package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda47 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda47(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4183lambda$sendSavePassword$41$orgtelegramuiPaymentFormActivity(tLObject, tL_error);
    }
}
