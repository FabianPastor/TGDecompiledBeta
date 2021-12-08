package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda47 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_payments_sendPaymentForm f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda47(PaymentFormActivity paymentFormActivity, TLRPC.TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_payments_sendPaymentForm;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3508lambda$sendData$53$orgtelegramuiPaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
