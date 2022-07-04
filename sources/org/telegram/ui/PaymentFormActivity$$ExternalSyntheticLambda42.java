package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda42(PaymentFormActivity paymentFormActivity, TLRPC.TL_error tL_error, boolean z, TLObject tLObject, String str) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_error;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.m4187lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
