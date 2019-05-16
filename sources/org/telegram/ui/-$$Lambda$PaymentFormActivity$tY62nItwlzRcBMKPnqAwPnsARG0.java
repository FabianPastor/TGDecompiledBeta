package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$tY62nItwlzRcBMKPnqAwPnsARG0 implements RequestDelegate {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ TL_account_getPassword f$2;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$tY62nItwlzRcBMKPnqAwPnsARG0(PaymentFormActivity paymentFormActivity, String str, TL_account_getPassword tL_account_getPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = str;
        this.f$2 = tL_account_getPassword;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkPassword$45$PaymentFormActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
