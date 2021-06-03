package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.-$$Lambda$PaymentFormActivity$xvFYd-kNGXcXnX60Q2a878DGgPc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PaymentFormActivity$xvFYdkNGXcXnX60Q2a878DGgPc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$PaymentFormActivity$xvFYdkNGXcXnX60Q2a878DGgPc INSTANCE = new $$Lambda$PaymentFormActivity$xvFYdkNGXcXnX60Q2a878DGgPc();

    private /* synthetic */ $$Lambda$PaymentFormActivity$xvFYdkNGXcXnX60Q2a878DGgPc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PaymentFormActivity.lambda$sendForm$46(tLObject, tLRPC$TL_error);
    }
}
