package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda30 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_account_password f$1;
    public final /* synthetic */ byte[] f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda30(PaymentFormActivity paymentFormActivity, TLRPC.TL_account_password tL_account_password, byte[] bArr) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_account_password;
        this.f$2 = bArr;
    }

    public final void run() {
        this.f$0.m3468lambda$checkPassword$56$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2);
    }
}
