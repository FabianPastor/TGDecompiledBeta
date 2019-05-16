package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$tcLMevZOApYaPGxte7o40Zbxk2c implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$tcLMevZOApYaPGxte7o40Zbxk2c(PaymentFormActivity paymentFormActivity, boolean z) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$29$PaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
