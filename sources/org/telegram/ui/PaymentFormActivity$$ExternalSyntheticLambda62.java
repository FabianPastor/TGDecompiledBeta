package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda62 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda62(PaymentFormActivity paymentFormActivity, boolean z) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendSavePassword$44(this.f$1, tLObject, tLRPC$TL_error);
    }
}
