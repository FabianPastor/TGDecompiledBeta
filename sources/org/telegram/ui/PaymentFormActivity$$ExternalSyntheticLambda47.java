package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_sendPaymentForm;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda47 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_payments_sendPaymentForm f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda47(PaymentFormActivity paymentFormActivity, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_sendPaymentForm tLRPC$TL_payments_sendPaymentForm) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_payments_sendPaymentForm;
    }

    public final void run() {
        this.f$0.lambda$sendData$57(this.f$1, this.f$2);
    }
}
