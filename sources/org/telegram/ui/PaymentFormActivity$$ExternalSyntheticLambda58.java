package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda58 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda58(PaymentFormActivity paymentFormActivity, TLObject tLObject) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendForm$54(this.f$1, tLObject, tLRPC$TL_error);
    }
}
