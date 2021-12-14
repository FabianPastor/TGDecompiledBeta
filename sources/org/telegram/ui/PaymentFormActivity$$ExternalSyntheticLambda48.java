package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda48 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda48(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPasswordInfo$33(tLObject, tLRPC$TL_error);
    }
}
