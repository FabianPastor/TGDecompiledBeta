package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda51 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$TL_account_getTmpPassword f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda51(PaymentFormActivity paymentFormActivity, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$TL_account_getTmpPassword;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkPassword$55(this.f$1, tLObject, tLRPC$TL_error);
    }
}
