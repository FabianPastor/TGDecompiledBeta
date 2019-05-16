package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$YjpXWVCXfyYjoMEGmUOokMuT9II implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$YjpXWVCXfyYjoMEGmUOokMuT9II(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendSavePassword$27$PaymentFormActivity(tLObject, tL_error);
    }
}
