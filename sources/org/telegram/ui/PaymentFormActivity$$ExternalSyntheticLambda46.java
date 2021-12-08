package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda46 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC.TL_account_getTmpPassword f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda46(PaymentFormActivity paymentFormActivity, TLRPC.TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tL_account_getTmpPassword;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3467lambda$checkPassword$55$orgtelegramuiPaymentFormActivity(this.f$1, tLObject, tL_error);
    }
}
