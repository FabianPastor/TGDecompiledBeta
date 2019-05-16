package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$WEnYGXUuB-UBrzR-7FlX-BgJs2A implements Runnable {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TL_account_getTmpPassword f$3;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$WEnYGXUuB-UBrzR-7FlX-BgJs2A(PaymentFormActivity paymentFormActivity, TLObject tLObject, TL_error tL_error, TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = tL_account_getTmpPassword;
    }

    public final void run() {
        this.f$0.lambda$null$41$PaymentFormActivity(this.f$1, this.f$2, this.f$3);
    }
}
