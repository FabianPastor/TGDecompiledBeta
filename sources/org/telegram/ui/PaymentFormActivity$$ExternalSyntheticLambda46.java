package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$Message[] f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda46(PaymentFormActivity paymentFormActivity, TLRPC$Message[] tLRPC$MessageArr) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$MessageArr;
    }

    public final void run() {
        this.f$0.lambda$sendData$50(this.f$1);
    }
}