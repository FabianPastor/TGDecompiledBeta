package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda38 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda38(PaymentFormActivity paymentFormActivity, TLObject tLObject, Runnable runnable) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$sendSavedForm$48(this.f$1, this.f$2);
    }
}
