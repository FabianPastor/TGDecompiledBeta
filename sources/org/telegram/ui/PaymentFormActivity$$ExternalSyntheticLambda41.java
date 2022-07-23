package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$Message f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda41(PaymentFormActivity paymentFormActivity, TLRPC$Message tLRPC$Message) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$sendData$56(this.f$1);
    }
}
