package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda57 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda57(PaymentFormActivity paymentFormActivity, Runnable runnable, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = runnable;
        this.f$2 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendSavedForm$51(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
