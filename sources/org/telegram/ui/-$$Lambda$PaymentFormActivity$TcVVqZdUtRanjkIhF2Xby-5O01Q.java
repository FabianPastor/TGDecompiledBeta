package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$TcVVqZdUtRanjkIhF2Xby-5O01Q implements Runnable {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ TL_account_getPassword f$4;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$TcVVqZdUtRanjkIhF2Xby-5O01Q(PaymentFormActivity paymentFormActivity, TL_error tL_error, TLObject tLObject, String str, TL_account_getPassword tL_account_getPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = tL_account_getPassword;
    }

    public final void run() {
        this.f$0.lambda$null$44$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
