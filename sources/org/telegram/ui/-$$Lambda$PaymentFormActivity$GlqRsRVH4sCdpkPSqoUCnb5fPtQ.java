package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$GlqRsRVH4sCdpkPSqoUCnb5fPtQ implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ TL_payments_sendPaymentForm f$1;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$GlqRsRVH4sCdpkPSqoUCnb5fPtQ(PaymentFormActivity paymentFormActivity, TL_payments_sendPaymentForm tL_payments_sendPaymentForm) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_payments_sendPaymentForm;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendData$40$PaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
