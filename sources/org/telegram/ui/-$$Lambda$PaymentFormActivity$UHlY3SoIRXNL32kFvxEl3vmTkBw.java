package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$UHlY3SoIRXNL32kFvxEl3vmTkBw implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ TLObject f$1;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$UHlY3SoIRXNL32kFvxEl3vmTkBw(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendForm$37$PaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
