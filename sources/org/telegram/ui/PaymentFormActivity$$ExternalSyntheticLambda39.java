package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda39 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$4;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda39(PaymentFormActivity paymentFormActivity, boolean z, String str, String str2, TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = tL_account_updatePasswordSettings;
    }

    public final void run() {
        this.f$0.m3519lambda$sendSavePassword$45$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
