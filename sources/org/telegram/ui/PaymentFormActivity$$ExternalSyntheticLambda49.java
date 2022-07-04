package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda49 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_account_getPassword f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda49(PaymentFormActivity paymentFormActivity, String str, TLRPC.TL_account_getPassword tL_account_getPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = str;
        this.f$2 = tL_account_getPassword;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4138lambda$checkPassword$63$orgtelegramuiPaymentFormActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
