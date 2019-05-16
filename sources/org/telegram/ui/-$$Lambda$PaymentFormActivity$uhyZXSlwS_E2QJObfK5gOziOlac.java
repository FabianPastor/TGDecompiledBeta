package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$uhyZXSlwS_E2QJObfK5gOziOlac implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ TL_account_getTmpPassword f$1;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$uhyZXSlwS_E2QJObfK5gOziOlac(PaymentFormActivity paymentFormActivity, TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_account_getTmpPassword;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$42$PaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
