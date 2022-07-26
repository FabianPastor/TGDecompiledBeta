package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda43(PaymentFormActivity paymentFormActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$sendSavePassword$41(this.f$1);
    }
}
