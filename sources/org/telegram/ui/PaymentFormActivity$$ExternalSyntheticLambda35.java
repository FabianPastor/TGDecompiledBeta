package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_account_getPassword f$4;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda35(PaymentFormActivity paymentFormActivity, TLRPC.TL_error tL_error, TLObject tLObject, String str, TLRPC.TL_account_getPassword tL_account_getPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = tL_account_getPassword;
    }

    public final void run() {
        this.f$0.m2811lambda$checkPassword$57$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
