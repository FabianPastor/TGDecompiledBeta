package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda41 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda41(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3502lambda$loadPasswordInfo$33$orgtelegramuiPaymentFormActivity(tLObject, tL_error);
    }
}
