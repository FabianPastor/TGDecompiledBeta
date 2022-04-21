package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLRPC.TL_account_getTmpPassword f$3;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda29(PaymentFormActivity paymentFormActivity, TLObject tLObject, TLRPC.TL_error tL_error, TLRPC.TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = tL_account_getTmpPassword;
    }

    public final void run() {
        this.f$0.m2808lambda$checkPassword$54$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, this.f$3);
    }
}
